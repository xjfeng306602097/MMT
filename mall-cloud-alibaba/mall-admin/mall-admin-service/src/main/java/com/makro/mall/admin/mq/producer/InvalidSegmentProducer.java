package com.makro.mall.admin.mq.producer;

import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @author xiaojunfeng
 * @description 过期segment生产者
 * @date 2021/11/10
 */
@Component
@Slf4j
public class InvalidSegmentProducer {

    @Autowired
    private PulsarTemplate pulsarTemplate;

    public void sendInvalidSegmentMessage(MmSegment segment, long delay) {
        if (delay <= 0L) {
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_INVALID_SEGMENT, segment);
        } else {
            pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_INVALID_SEGMENT, segment, delay);
        }
    }


    public void sendInvalidSegmentMessage(MmSegment segment, LocalDateTime workTime) {
        Duration duration = java.time.Duration.between(LocalDateTime.now(), workTime);
        if (duration.toSeconds() < 0) {
            this.sendInvalidSegmentMessage(segment, 0);
            log.info("sendInvalidSegmentMessage segmentId{}将马上失效", segment.getId());
        } else {
            this.sendInvalidSegmentMessage(segment, duration.toSeconds());
            log.info("sendInvalidSegmentMessage segmentId{}将在{}分钟后失效", segment.getId(), duration.toMinutes());

        }
    }
}
