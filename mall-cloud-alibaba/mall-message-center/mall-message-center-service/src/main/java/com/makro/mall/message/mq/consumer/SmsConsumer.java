package com.makro.mall.message.mq.consumer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.component.sms.SmsChainContext;
import com.makro.mall.message.pojo.dto.GGGSmsMessageDTO;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description 消费者配置
 * @date 2021/11/10
 */
@Component
@Slf4j
public class SmsConsumer {

    @Resource
    private SmsChainContext smsChainContext;
    @Resource
    private RedissonClient redissonClient;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_SMS_GGG, clazz = GGGSmsMessageDTO.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeSmsGgg(GGGSmsMessageDTO messageDTO) {
        try {
            if (redissonClient.getLock("lock:sms:ggg:" + messageDTO.getId()).tryLock(0, 60, TimeUnit.SECONDS)) {
                log.info("consumeSmsGgg 接收到消息，消息体{}", messageDTO);
                smsChainContext.process(messageDTO);
                log.info("consumeSmsGgg 处理消息完毕，消息体{}", messageDTO);
            }
        } catch (InterruptedException e) {
            log.error("consumeSmsGgg error", e);
        }
    }

}
