package com.makro.mall.message.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.api.fallback.MessagePropertieseignClientFallBack;
import com.makro.mall.message.pojo.entity.MessageProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2022/3/10
 */
@FeignClient(value = "makro-message", fallback = MessagePropertieseignClientFallBack.class)
public interface MessagePropertiesFeignClient {

    @GetMapping("/api/v1/properties/client/{id}")
    BaseResponse<MessageProperties> detail(@PathVariable String id);

}
