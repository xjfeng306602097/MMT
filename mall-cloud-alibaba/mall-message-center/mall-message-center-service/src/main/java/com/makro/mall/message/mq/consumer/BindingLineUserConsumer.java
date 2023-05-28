package com.makro.mall.message.mq.consumer;

import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.message.pojo.dto.BindingLineUserDTO;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

/**
 * 功能描述:
 *
 * @Param:
 * @Return:
 * @Author: 卢嘉俊
 * @Date: 2022/8/1 Line
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BindingLineUserConsumer {

    private final CustomerFeignClient customerFeignClient;

    @PulsarConsumer(topic = PulsarConstants.BINDING_LINE_USER_ID, clazz = BindingLineUserDTO.class, subscriptionType = {SubscriptionType.Failover})
    public void bindingLineUser(BindingLineUserDTO dto) {
//        String decode = AesBase62Util.decode(dto.getState());
//        if (StrUtil.isBlank(decode)) {
//            return;
//        }
//        Long userId = Long.valueOf(decode);
//        log.info("bindingLineUser接收到消息，消息体{},userId:{}", dto, userId);
//        MmCustomerVO data = customerFeignClient.detail(userId).getData();
//        if (data.getLineId() == null) {
//            MmCustomerVO customerVO = new MmCustomerVO();
//            customerVO.setLineId(dto.getLineUserId());
//            customerFeignClient.patch(userId, customerVO);
//        }
//        log.info("bindingLineUser处理消息完毕，消息体{},userId:{}", dto, userId);
    }


}
