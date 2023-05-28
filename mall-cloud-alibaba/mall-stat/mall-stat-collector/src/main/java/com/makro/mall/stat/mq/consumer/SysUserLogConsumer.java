package com.makro.mall.stat.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import com.makro.mall.stat.pojo.entity.SystemUserLog;
import com.makro.mall.stat.service.SystemUserLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xiaojunfeng
 * @description clickhouse sink consumer for pulsar
 * @date 2022/7/7
 * @date 2022/9/7 补充会员类型
 */
@Component
@Slf4j
public class SysUserLogConsumer {

    @Resource
    private SystemUserLogService systemUserLogService;


    @PulsarConsumer(topic = PulsarConstants.TOPIC_SYSTEM_USER_LOG, clazz = SystemUserLog.class, subscriptionType = {SubscriptionType.Failover})
    public void systemUserLog(SystemUserLog systemUserLog) {
        try {
            log.info("systemUserLog 接收到消息，systemUserLog{}", JSON.toJSONString(systemUserLog));
            systemUserLogService.save(systemUserLog);
            log.info("systemUserLog 处理消息完毕，systemUserLog{}", JSON.toJSONString(systemUserLog));
        } catch (Exception e) {
            log.error("systemUserLog 处理消息异常，systemUserLog{} e{}", JSON.toJSONString(systemUserLog), e);
        }
    }

    @PulsarConsumer(topic = PulsarConstants.TOPIC_SYSTEM_USER_LOG_JSON, subscriptionType = {SubscriptionType.Failover})
    public void systemUserLogJson(byte[] systemUserLog) {
        try {
            SystemUserLog o = JSON.parseObject(systemUserLog, SystemUserLog.class);
            log.info("systemUserLogJson 接收到消息，systemUserLogJson{}", JSON.toJSONString(systemUserLog));
            systemUserLogService.save(o);
            log.info("systemUserLogJson 处理消息完毕，systemUserLogJson{}", JSON.toJSONString(systemUserLog));
        } catch (Exception e) {
            log.error("systemUserLogJson 处理消息异常，systemUserLogJson{} e{}", JSON.toJSONString(systemUserLog), e);
        }
    }

}
