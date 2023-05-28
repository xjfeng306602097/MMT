package com.makro.mall.message.mq.producer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.dto.BindingLineUserDTO;
import com.makro.mall.message.pojo.dto.GGGSmsMessageDTO;
import com.makro.mall.message.pojo.dto.H5MailMessageDTO;
import com.makro.mall.message.pojo.dto.TextMailMessageDTO;
import com.makro.mall.message.pojo.entity.LineSendMessage;
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
        return new ProducerFactory().addProducer(PulsarConstants.TOPIC_MAIL_MESSAGE_H5, H5MailMessageDTO.class)
                .addProducer(PulsarConstants.TOPIC_MAIL_MESSAGE_TEXT, TextMailMessageDTO.class)
                .addProducer(PulsarConstants.BINDING_LINE_USER_ID, BindingLineUserDTO.class)
                .addProducer(PulsarConstants.TOPIC_LINE_MULTICAST_FLEX, LineSendMessage.class)
                .addProducer(PulsarConstants.TOPIC_SMS_GGG, GGGSmsMessageDTO.class);
    }

}
