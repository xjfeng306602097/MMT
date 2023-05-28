package com.makro.mall.message.mq.producer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.dto.H5MailMessageDTO;
import com.makro.mall.message.pojo.dto.TextMailMessageDTO;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description 邮件消息生产者
 * @date 2021/11/10
 */
@Component
@Slf4j
public class MailMessageProducer {

    @Autowired
    private PulsarTemplate pulsarTemplate;

    public void sendH5MailMessage(H5MailMessageDTO dto, long delay) {
        if (delay == 0L) {
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_MAIL_MESSAGE_H5, dto);
        } else {
            pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_MAIL_MESSAGE_H5, dto, delay);
        }
    }

    public void sendTextMailMessage(TextMailMessageDTO dto, long delay) {
        if (delay == 0L) {
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_MAIL_MESSAGE_TEXT, dto);
        } else {
            pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_MAIL_MESSAGE_TEXT, dto, delay);
        }
    }


}
