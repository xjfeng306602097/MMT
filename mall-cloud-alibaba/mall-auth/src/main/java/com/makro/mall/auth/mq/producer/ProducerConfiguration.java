package com.makro.mall.auth.mq.producer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.producer.ProducerFactory;
import com.makro.mall.stat.pojo.entity.SystemUserLog;
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
        return new ProducerFactory().addProducer(PulsarConstants.TOPIC_SYSTEM_USER_LOG, SystemUserLog.class);
    }

}
