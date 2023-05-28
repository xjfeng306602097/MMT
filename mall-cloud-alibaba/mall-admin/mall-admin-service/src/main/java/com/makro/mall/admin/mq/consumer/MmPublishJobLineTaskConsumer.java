package com.makro.mall.admin.mq.consumer;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmPublishJobLineTask;
import com.makro.mall.admin.pojo.entity.MmPublishJobTaskLog;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.admin.service.MmPublishJobLineTaskService;
import com.makro.mall.admin.service.MmPublishJobTaskLogService;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.message.api.LineFeignClient;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 功能描述:
 * Mail发布工程
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/1
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MmPublishJobLineTaskConsumer {

    private final MmPublishJobLineTaskService mmPublishJobLineTaskService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;
    private final LineFeignClient lineFeignClient;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MmActivityService mmActivityService;
    @Value("${line.login.clientId}")
    public String loginClientId;
    @Value("${line.login.callbackUrl}")
    public String loginCallbackUrl;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_PUBLISH_JOB_LINE_TASK, subscriptionType = {SubscriptionType.Shared})
    public void consumeMmPublishJobLineTask(byte[] bytes) {
        String id = new String(bytes);
        try {
            if (redissonClient.getLock("lock:line:job:" + id).tryLock(0, 1, TimeUnit.MINUTES)) {
                log.info("Line-发布任务接收到消息，id: {}", id);
                MmPublishJobLineTask task = mmPublishJobLineTaskService.getById(id);
                //乐观锁
                boolean update = mmPublishJobLineTaskService.update(new LambdaUpdateWrapper<MmPublishJobLineTask>()
                        .set(MmPublishJobLineTask::getStatus, MessageTaskEnum.PUSHING.getStatus())
                        .eq(MmPublishJobLineTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus())
                        .eq(MmPublishJobLineTask::getId, id));
                if (!update) {
                    log.info("Line-发布任务乐观锁争抢失败，id: {}", id);
                    return;
                }


                boolean isBroadcast = StrUtil.equals(task.getMmCustomerId(), "0");
                if (isBroadcast) {
                    lineFeignClient.broadcast(task.getTemplate());
                } else {
                    //List<MmPublishJobTaskLog> mmCustomers = mmPublishJobTaskLogService.list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                    //        .eq(MmPublishJobTaskLog::getChannel, "line")
                    //        .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                    //        .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
                    List<String> split = StrUtil.split(task.getMmCustomerId(), ",");
                    //将List切成500个key唯一组 上限60个
                    int splitNum = 500;
                    List<List<String>> splitCustomerId = Stream.iterate(0, n -> n + 1)
                            .limit((split.size() + splitNum - 1) / splitNum)
                            .parallel()
                            .map(a -> split.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                            .filter(b -> !b.isEmpty())
                            .collect(Collectors.toList());

                    long failed = mmPublishJobTaskLogService.count(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                            .eq(MmPublishJobTaskLog::getChannel, "line")
                            .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                            .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.FAILED.getStatus()));
                    redisTemplate.opsForValue().setIfAbsent("line:job:" + id + ":fail", 0L, 30, TimeUnit.MINUTES);
                    redisTemplate.opsForValue().setIfAbsent("line:job:" + id + ":total", split.size() - failed, 30, TimeUnit.MINUTES);

                    MmActivity activity = mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, task.getMmCode()));
                    if (Long.valueOf(MessageSendEnum.CANCELED.getStatus()).equals(mmPublishJobLineTaskService.getById(task.getId()).getStatus())) {
                        log.info("Line-发布任务处理消息失败，任务取消，MmPublishJobLineTask::{}", task);
                        return;
                    }
                    splitCustomerId.forEach(x -> mmPublishJobLineTaskService.send(task, x, activity));

                }
                log.info("Line-发布任务处理消息完毕，MmPublishJobLineTask: {}", task);

            }
        } catch (Exception e) {
            //异常输出
            log.error("Line-发布任务 error:{}", ExceptionUtil.stacktraceToString(e));
            mmPublishJobLineTaskService.update(new LambdaUpdateWrapper<MmPublishJobLineTask>()
                    .set(MmPublishJobLineTask::getStatus, MessageTaskEnum.FAILED.getStatus())
                    .eq(MmPublishJobLineTask::getId, id));
            mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                    .set(MmPublishJobTaskLog::getStatus, MessageSendEnum.FAILED.getStatus())
                    .eq(MmPublishJobTaskLog::getTaskId, id));
        }
    }

}
