package com.makro.mall.message.mq.consumer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.entity.UserAction;
import com.makro.mall.message.repository.UserActionRepository;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class UserActionConsumer {

    @Resource
    private UserActionRepository userActionRepository;

    @PulsarConsumer(topic = PulsarConstants.TOPIC_USER_ACTION, clazz = UserAction.class, subscriptionType = {SubscriptionType.Failover})
    public void consumeUserAction(UserAction userAction) {
        try {
            log.info("consumeUserAction 接收到消息，消息体{}", userAction);
            userActionRepository.save(userAction);
            log.info("consumeUserAction 处理消息完毕，消息体{}", userAction);
        } catch (Exception e) {
            log.error("consumeUserAction error", e);
        }
    }

}
