package com.makro.mall.message.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.message.api.MessagePropertiesFeignClient;
import com.makro.mall.message.pojo.entity.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/5/7
 */
@Component
@Slf4j
public class MessagePropertieseignClientFallBack implements MessagePropertiesFeignClient {

    @Override
    public BaseResponse<MessageProperties> detail(String id) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
