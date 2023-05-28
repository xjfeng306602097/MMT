package com.makro.mall.admin.mq.producer;

import com.makro.mall.admin.pojo.dto.PublishJobTaskDTO;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.entity.StatView;
import com.makro.mall.pulsar.producer.ProducerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaojunfeng
 * @description 生产者配置
 * @date 2021/11/10
 */
@Configuration
public class ProducerConfiguration {

    @Bean
    public ProducerFactory producerFactory() {
        return new ProducerFactory()
                .addProducer(PulsarConstants.TOPIC_STAT_VIEW, StatView.class)
                .addProducer(PulsarConstants.TOPIC_PUBLISH_JOB_EMAIL_TASK)
                .addProducer(PulsarConstants.TOPIC_PUBLISH_JOB_LINE_TASK)
                .addProducer(PulsarConstants.TOPIC_MM_SAVE_ROLL_BACK)
                .addProducer(PulsarConstants.TOPIC_PUBLISH_JOB_SMS_TASK, PublishJobTaskDTO.class)
                .addProducer(PulsarConstants.TOPIC_INVALID_SEGMENT, MmSegment.class);
    }

}
