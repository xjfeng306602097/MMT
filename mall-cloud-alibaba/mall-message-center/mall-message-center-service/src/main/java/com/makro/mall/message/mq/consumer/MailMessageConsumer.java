package com.makro.mall.message.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.component.mail.MailChainContext;
import com.makro.mall.message.pojo.dto.H5MailMessageDTO;
import com.makro.mall.message.pojo.dto.TextMailMessageDTO;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description 消费者配置
 * @date 2021/11/10
 */
@Component
@Slf4j
public class MailMessageConsumer {

    @Autowired
    private MailChainContext mailChainContext;
    @Autowired
    private RedissonClient redissonClient;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_MAIL_MESSAGE_H5, clazz = H5MailMessageDTO.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeH5(H5MailMessageDTO messageDTO) {
        try {
            if (redissonClient.getLock("lock:mail:message:h5:" + messageDTO.getId()).tryLock(0, 60, TimeUnit.SECONDS)) {
                log.info("consumeH5 接收到消息，消息体{}", JSON.toJSONString(messageDTO));
                mailChainContext.process(messageDTO);
                log.info("consumeH5 处理消息完毕，消息体{}", JSON.toJSONString(messageDTO));
            }
        } catch (InterruptedException e) {
            log.error("consumeH5 邮件发送队列处理消息失败", e);
        }
    }

    /**
     * 要使用延时消息，必须指定消息类型为Shared
     *
     * @param messageDTO
     */
    @PulsarConsumer(topic = PulsarConstants.TOPIC_MAIL_MESSAGE_TEXT, clazz = TextMailMessageDTO.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeText(TextMailMessageDTO messageDTO) {
        try {
            if (redissonClient.getLock("lock:mail:message:text:" + messageDTO.getId()).tryLock(0, 60, TimeUnit.SECONDS)) {
                log.info("consumeText 接收到消息，消息体{}", messageDTO);
                mailChainContext.process(messageDTO);
                log.info("consumeText 处理消息完毕，消息体{}", messageDTO);
            }
        } catch (InterruptedException e) {
            log.error("consumeText 邮件发送队列处理消息失败", e);
        }
    }

}
