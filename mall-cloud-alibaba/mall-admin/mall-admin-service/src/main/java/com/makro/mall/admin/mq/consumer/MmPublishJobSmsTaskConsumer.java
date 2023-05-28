package com.makro.mall.admin.mq.consumer;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.makro.mall.admin.pojo.dto.PublishJobTaskDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJobSmsTask;
import com.makro.mall.admin.pojo.entity.MmPublishJobTaskLog;
import com.makro.mall.admin.service.MmPublishJobSmsTaskService;
import com.makro.mall.admin.service.MmPublishJobTaskLogService;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;


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
public class MmPublishJobSmsTaskConsumer {


    private final MmPublishJobSmsTaskService mmPublishJobSmsTaskService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_PUBLISH_JOB_SMS_TASK, clazz = PublishJobTaskDTO.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeMmPublishJobSmsTask(PublishJobTaskDTO publishJobTaskDTO) {
        String id = publishJobTaskDTO.getTaskId();
        try {
            if (redissonClient.getLock("lock:sms:job:" + id).tryLock(0, 1, TimeUnit.MINUTES)) {
                log.info("sms-发布任务接收到消息，id: {}", id);
                MmPublishJobSmsTask task = mmPublishJobSmsTaskService.getById(id);
                //乐观锁
                boolean update = mmPublishJobSmsTaskService.update(new LambdaUpdateWrapper<MmPublishJobSmsTask>()
                        .set(MmPublishJobSmsTask::getStatus, MessageTaskEnum.PUSHING.getStatus())
                        .eq(MmPublishJobSmsTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus())
                        .eq(MmPublishJobSmsTask::getId, id));
                if (!update) {
                    log.info("sms-发布任务乐观锁争抢失败，id: {}", id);
                    return;
                }

                List<MmPublishJobTaskLog> mmCustomers = mmPublishJobTaskLogService.list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                        .eq(MmPublishJobTaskLog::getChannel, "sms")
                        .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                        .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));

                long failed = mmPublishJobTaskLogService.count(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                        .eq(MmPublishJobTaskLog::getChannel, "sms")
                        .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                        .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.FAILED.getStatus()));
                redisTemplate.opsForValue().setIfAbsent("sms:job:" + id + ":fail", 0L, 30, TimeUnit.MINUTES);
                redisTemplate.opsForValue().setIfAbsent("sms:job:" + id + ":total", mmCustomers.size() - failed, 30, TimeUnit.MINUTES);
                mmCustomers.forEach(c -> mmPublishJobSmsTaskService.send(publishJobTaskDTO.getCreatedBy(), task, c));
                log.info("sms-发布任务处理消息完毕，MmPublishJobSmsTask: {}", task);
            }
        } catch (Exception e) {
            //异常输出
            log.error("sms-发布任务 error:{}", ExceptionUtil.stacktraceToString(e));
            mmPublishJobSmsTaskService.update(new LambdaUpdateWrapper<MmPublishJobSmsTask>()
                    .set(MmPublishJobSmsTask::getStatus, MessageTaskEnum.FAILED.getStatus())
                    .eq(MmPublishJobSmsTask::getId, id));
            mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                    .set(MmPublishJobTaskLog::getStatus, MessageSendEnum.FAILED.getStatus())
                    .eq(MmPublishJobTaskLog::getTaskId, id));
        }
    }


}
