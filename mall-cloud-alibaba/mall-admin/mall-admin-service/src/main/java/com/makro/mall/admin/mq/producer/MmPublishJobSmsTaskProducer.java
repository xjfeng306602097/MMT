package com.makro.mall.admin.mq.producer;

import com.makro.mall.admin.pojo.dto.PublishJobTaskDTO;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/31
 */
@Component
@Slf4j
public class MmPublishJobSmsTaskProducer {

    @Resource
    private PulsarTemplate pulsarTemplate;

    public void sendMmPublishJobSmsTaskMessage(String taskId, LocalDateTime workTime) {
        Duration duration = java.time.Duration.between(LocalDateTime.now(), workTime);
        this.sendMmPublishJobSmsTaskMessage(taskId, duration.toSeconds());
        log.info("sms-publishJobTaskId为{}的任务将在{}分钟后进行", taskId, duration.toMinutes());
    }

    public void sendMmPublishJobSmsTaskMessage(String publishJobTaskId, long delay) {
        //5分钟内马上发送
        if (delay <= 300L) {
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_PUBLISH_JOB_SMS_TASK, new PublishJobTaskDTO(publishJobTaskId));
        } else {
            pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_PUBLISH_JOB_SMS_TASK, new PublishJobTaskDTO(publishJobTaskId), delay);
        }
    }


}
