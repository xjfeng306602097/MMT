package com.makro.mall.admin.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Ljj
 */
@Component
@Slf4j
public class MmSaveRollBackProducer {

    @Autowired
    private PulsarTemplate<byte[]> pulsarTemplate;

    public void sendSaveRollBackMessage(String mmCode) {
        pulsarTemplate.deliverAfterSecAsync(PulsarConstants.TOPIC_MM_SAVE_ROLL_BACK, StrUtil.utf8Bytes(mmCode), 60L);
    }


}
