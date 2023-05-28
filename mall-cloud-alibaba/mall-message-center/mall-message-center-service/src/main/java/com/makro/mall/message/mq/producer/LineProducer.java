package com.makro.mall.message.mq.producer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/8 Line
 */
@Component
@Slf4j
public class LineProducer {

    @Autowired
    private PulsarTemplate pulsarTemplate;

    public void multicastFlex(LineSendMessage message, long delay) {
        log.info("LineProducer 发送消息 message:{}", message);
        if (delay == 0L) {
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_LINE_MULTICAST_FLEX, message);
        } else {
            pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_LINE_MULTICAST_FLEX, message, delay);
        }
    }


}
