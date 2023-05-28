package com.makro.mall.message.mq.consumer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.entity.StatView;
import com.makro.mall.message.repository.StatViewRepository;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description 视图统计
 * @date 2022/3/23
 */
@Component
@Slf4j
public class StatViewConsumer {

    @Autowired
    private StatViewRepository statViewRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_STAT_VIEW, clazz = StatView.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeStatView(StatView statView) {
        log.info("接收到消息，消息体{}", statView);
        statViewRepository.save(statView);
        log.info("处理消息完毕，消息体{}", statView);
    }

}
