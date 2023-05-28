package com.makro.mall.admin.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @author xiaojunfeng
 * @description 邮件消息生产者
 * @date 2021/11/10
 */
@Component
@Slf4j
public class MmPublishJobLineTaskProducer {

    @Autowired
    private PulsarTemplate pulsarTemplate;

    public void sendMmPublishJobLineTaskMessage(String publishJobTaskId, long delay) {
        byte[] bytes = StrUtil.utf8Bytes(publishJobTaskId);
        //5分钟内马上发送
        if (delay <= 300L) {
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_PUBLISH_JOB_LINE_TASK, bytes);
        } else {
            pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_PUBLISH_JOB_LINE_TASK, bytes, delay);
        }
    }

    public void sendMmPublishJobLineTaskMessage(String taskId, LocalDateTime workTime) {
        Duration duration = Duration.between(LocalDateTime.now(), workTime);
        this.sendMmPublishJobLineTaskMessage(taskId, duration.toSeconds());
        log.info("publishLineJobTaskId为{}的Line任务将在{}分钟后进行", taskId, duration.toMinutes());
    }
}
