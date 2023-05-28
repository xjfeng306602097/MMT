package com.makro.mall.message.mq.consumer;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskMonitorDTO;
import com.makro.mall.admin.pojo.dto.UserTaskMonitorDTO;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.message.mq.producer.LineProducer;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.repository.LineSendMessageRepository;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 功能描述:
 *
 * @Return:
 * @Author: 卢嘉俊
 * @Date: 2022/8/8 Line
 */
@Component
@Slf4j
public class LineConsumer {

    @Resource
    private LineSDK lineSDK;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private LineSendMessageRepository lineSendMessageRepository;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private PublishFeignClient publishFeignClient;
    @Resource
    private LineProducer lineProducer;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_LINE_MULTICAST_FLEX, clazz = LineSendMessage.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeLineMulticastFlex(LineSendMessage lineSendMessage) {
        try {
            if (redissonClient.getLock("lock:line:multicast:flex:" + lineSendMessage.getId()).tryLock(0, 60, TimeUnit.SECONDS)) {
                log.info("consumeLineMulticastFlex 接收到消息，消息体{}", lineSendMessage);
                String body = lineSDK.multicastFlex(lineSendMessage.getOtherMessage());
                //如果次数用完则20分钟后发
                if (StrUtil.contains(body, "Too Many Requests")) {
                    log.error("次数用完20分钟后发 lineSendMessage:{}", lineSendMessage);
                    lineProducer.multicastFlex(lineSendMessage, 1200);
                }
                MessageSendEnum status = StrUtil.equals("{}", body) ? MessageSendEnum.SUCCEEDED : MessageSendEnum.FAILED;
                lineSendMessage.setStatus(status);
                lineSendMessage.setBody(body);

                //记录日志
                lineSendMessageRepository.save(lineSendMessage);

                log.info("consumeLineMulticastFlex 记录日志完成，消息体{}", lineSendMessage);
                //如果为任务发布则更新任务状态
                updateTaskUserStatus(lineSendMessage.getMmPublishJobLineTaskId(), status, lineSendMessage.getTo());
                updateTaskStatus(lineSendMessage.getMmPublishJobLineTaskId(), status, lineSendMessage.getTo());
                log.info("consumeLineMulticastFlex 处理消息完毕，消息体{}", lineSendMessage);
            }
        } catch (Exception e) {
            log.error("consumeLineMulticastFlex error", e);
            updateTaskUserStatus(lineSendMessage.getMmPublishJobLineTaskId(), MessageSendEnum.FAILED, lineSendMessage.getTo());
            updateTaskStatus(lineSendMessage.getMmPublishJobLineTaskId(), MessageSendEnum.FAILED, lineSendMessage.getTo());
        }
    }

    private void updateTaskUserStatus(String id, MessageSendEnum status, Set<LineSendMessage.LineCustomer> to) {
        if (StrUtil.isNotEmpty(id)) {
            log.info("line 更新用户任务状态为:{} 用户:{} taskId:{}", status, to, id);
            UserTaskMonitorDTO dto = new UserTaskMonitorDTO();
            dto.setTaskId(id);
            dto.setStatus(status.getStatus());
            dto.setChannel("line");
            dto.setSendToForLine(to.stream().map(LineSendMessage.LineCustomer::getLineId).collect(Collectors.toList()));
            publishFeignClient.updateUserState(dto);
        }
    }


    /**
     * 功能描述:
     *
     * @Param: 更新任务状态
     * @Return:
     * @Author: 卢嘉俊
     * @Date: 2022/9/20 发布任务
     */
    private void updateTaskStatus(String id, MessageSendEnum status, Set<LineSendMessage.LineCustomer> to) {
        if (StrUtil.isNotEmpty(id) && ObjectUtil.isNotNull(redisUtils.get(getKey(id, ":total")))) {
            if (ObjectUtil.equals(status, MessageSendEnum.FAILED)) {
                //失败
                log.info("line 发送给用户:{} 失败 taskId:{}", to, id);
                redisUtils.incr(getKey(id, ":fail"), to.size());
            }
            long total = redisUtils.decr(getKey(id, ":total"), to.size());
            if (ObjectUtil.equal(total, 0L)) {
                Integer fail = (Integer) redisUtils.get(getKey(id, ":fail"));
                MmPublishJobTaskMonitorDTO dto1 = new MmPublishJobTaskMonitorDTO();
                dto1.setId(id);
                dto1.setChannel("line");

                //status -> 0-发布失败,1-未发布,2-发布中,3-发布成功,4-发布部分成功
                if (ObjectUtil.equal(fail, 0)) {
                    //如果失败为0则为全部成功
                    dto1.setStatus(MessageTaskEnum.SUCCEEDED.getStatus());
                } else if (ObjectUtil.equal(total, 0L)) {
                    //总数-失败数不为0则为部分成功
                    dto1.setStatus(MessageTaskEnum.FAILED.getStatus());
                    List<LineSendMessage> lineSendMessages = lineSendMessageRepository.findAllByMmPublishJobLineTaskId(id);
                    lineSendMessages.forEach(x -> {
                        log.info("updateTaskStatus 查询任务状态 lineSendMessages:{}", x);
                        if (MessageSendEnum.SUCCEEDED.equals(x.getStatus())) {
                            dto1.setStatus(MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus());
                        }
                    });
                } else {
                    //失败
                    dto1.setStatus(MessageTaskEnum.FAILED.getStatus());
                }
                publishFeignClient.updateState(dto1);
            }
        }
    }

    @NotNull
    private String getKey(String id, String x) {
        return "line:job:" + id + x;
    }


}
