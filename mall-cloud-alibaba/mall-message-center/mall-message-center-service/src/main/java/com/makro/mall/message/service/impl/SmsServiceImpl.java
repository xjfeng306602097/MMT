package com.makro.mall.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskMonitorDTO;
import com.makro.mall.admin.pojo.dto.UserTaskMonitorDTO;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.message.enums.SmsChannelEnum;
import com.makro.mall.message.mq.producer.SmsProducer;
import com.makro.mall.message.pojo.dto.GGGSmsMessageDTO;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.repository.SmsRepository;
import com.makro.mall.message.service.SmsService;
import com.makro.mall.message.vo.MessagePageReqVO;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final SmsRepository smsRepository;

    private final SmsProducer smsProducer;

    private final RedisUtils redisUtils;
    private final PublishFeignClient publishFeignClient;

    @Override
    public Page<SmsMessage> page(List<Integer> status, String sender, Date begin, Date end, Integer page, Integer size, Integer isDelete) {
        return smsRepository.page(status, sender, begin, end, page, size, isDelete);
    }

    @Override
    public SmsMessage findFirstById(String id) {
        return smsRepository.findFirstById(id);
    }

    @Override
    public SmsMessage save(SmsMessage message) {
        Assert.isTrue(CollectionUtil.isNotEmpty(message.getReceivers()), StatusCode.TOUSER_NOT_NULL);
        Assert.isTrue(StrUtil.isNotBlank(message.getMsg()), StatusCode.SUBJECT_NOT_NULL);
        // 设置为未删除
        message.setIsDelete(0);
        // 初始化状态待发送
        message.setStatus(MessageSendEnum.NOT_SENT);
        message = smsRepository.save(message);
        SmsMessage finalMessage = message;
        if (message.getDelay() < SmsMessage.MAIL_SEND_DELAY_THRESHOLD) {
            CompletableFuture.runAsync(() -> {
                if (finalMessage.getChannelEnum() == SmsChannelEnum.GGG_MULTIPLE) {
                    GGGSmsMessageDTO text = new GGGSmsMessageDTO();
                    BeanUtil.copyProperties(finalMessage, text);
                    smsProducer.sendGGGSmsMsg(text, finalMessage.getDelay());
                }
            }).exceptionally(e -> {
                log.error("SmsServiceImpl save执行失败 MailMessage:{} 原因:{}", JSON.toJSONString(finalMessage), e.toString());
                // 发送失败,异常处理,修改消息状态
                SmsMessage smsMessage = new SmsMessage();
                smsMessage.setId(finalMessage.getId());
                smsMessage.setStatus(MessageSendEnum.FAILED);
                update(smsMessage);

                updateTaskStatus(finalMessage.getMmPublishJobSmsTaskId(), MessageSendEnum.FAILED, finalMessage.getReceivers());
                return null;
            });
            ;
        }
        return message;
    }

    @Override
    public SmsMessage update(SmsMessage message) {
        SmsMessage dbMailMessage = smsRepository.findFirstById(message.getId());
        Assert.isTrue(dbMailMessage != null, StatusCode.EMAIL_NOT_EXISTS);
        BeanUtil.copyProperties(message, dbMailMessage, CopyOptions.create().ignoreNullValue());
        smsRepository.save(dbMailMessage);
        return dbMailMessage;
    }

    @Override
    public Long removeByIds(List<String> ids) {
        Query query = Query.query(Criteria.where("id").in(ids));
        Update update = Update.update("isDelete", 1);
        UpdateResult updateResult = smsRepository.mongoTemplate().updateMulti(query, update, MailMessage.class);
        return updateResult.getModifiedCount();
    }

    @Override
    public Page<SmsMessage> smsUserPage(MessagePageReqVO vo) {
        return smsRepository.smsUserPage(vo);
    }

    /**
     * 功能描述:
     *
     * @Param: 更新任务状态
     * @Return:
     * @Author: 卢嘉俊
     * @Date: 2022/9/20 发布任务
     */
    @Override
    public void updateTaskStatus(Long id, MessageSendEnum status, List<String> receivers) {
        if (ObjectUtil.isNotNull(id) && ObjectUtil.isNotNull(redisUtils.get("sms:job:" + id + ":total"))) {
            if (ObjectUtil.notEqual(status, MessageSendEnum.SUCCEEDED)) {
                //失败
                log.info("sms 发送给用户:{} 失败 taskId:{}", receivers, id);
                redisUtils.incr("sms:job:" + id + ":fail", 1);
            }
            long total = redisUtils.decr("sms:job:" + id + ":total", 1);
            if (ObjectUtil.equal(total, 0L)) {
                Integer fail = (Integer) redisUtils.get("sms:job:" + id + ":fail");
                MmPublishJobTaskMonitorDTO dto1 = new MmPublishJobTaskMonitorDTO();
                dto1.setId(String.valueOf(id));
                dto1.setChannel("sms");

                //status -> 0-发布失败,1-未发布,2-发布中,3-发布成功,4-发布部分成功
                if (ObjectUtil.equal(fail, 0)) {
                    //如果失败为0则为全部成功
                    dto1.setStatus(MessageTaskEnum.SUCCEEDED.getStatus());
                } else if (ObjectUtil.equal(total, 0L)) {
                    //总数-失败数不为0则为部分成功
                    dto1.setStatus(MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus());
                } else {
                    //失败
                    dto1.setStatus(MessageTaskEnum.FAILED.getStatus());
                }
                publishFeignClient.updateState(dto1);
            }
        }
    }

    @Override
    public void updateTaskUserStatus(Long id, MessageSendEnum status, List<String> receivers) {
        if (ObjectUtil.isNotNull(id)) {
            log.info("sms 更新任务状态为:{} 用户:{} taskId:{}", status, receivers, id);
            UserTaskMonitorDTO dto = new UserTaskMonitorDTO();
            dto.setTaskId(String.valueOf(id));
            dto.setStatus(status.getStatus());
            dto.setChannel("sms");
            dto.setSendTo(receivers.stream().findFirst().get());
            publishFeignClient.updateUserState(dto);
        }
    }
}
