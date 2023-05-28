package com.makro.mall.gateway.mq.producer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author xiaojunfeng
 * @description 过期segment生产者
 * @date 2021/11/10
 */
@Component
@Slf4j
public class SysUserLogProducer {

    @Autowired
    private PulsarTemplate<byte[]> pulsarTemplate;

    public void save(byte[] systemUserLog) {
        pulsarTemplate.sendAsync(PulsarConstants.TOPIC_SYSTEM_USER_LOG_JSON, systemUserLog);
    }


}
