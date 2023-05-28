package com.makro.mall.message.mq.producer;

import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.dto.BindingLineUserDTO;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 功能描述: 用于绑定line UserId到用户表
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/2
 */
@Component
@Slf4j
public class BindingLineUserProducer {

    @Autowired
    private PulsarTemplate pulsarTemplate;

    public void bindingLineUser(BindingLineUserDTO dto) {
        pulsarTemplate.sendAsync(PulsarConstants.BINDING_LINE_USER_ID, dto);
    }


}
