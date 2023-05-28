package com.makro.mall.message.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.api.fallback.SmsFeignClientFallBack;
import com.makro.mall.message.dto.SmsMessagePageFeignDTO;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2022/3/10
 */
@FeignClient(value = "makro-message", fallback = SmsFeignClientFallBack.class, contextId = "sms-client")
public interface SmsFeignClient {

    @PostMapping("/api/v1/sms")
    BaseResponse add(@RequestBody SmsMessage smsMessage);

    @PostMapping("/api/v1/sms/smsUserPage")
    BaseResponse<SmsMessagePageFeignDTO> smsUserPage(@RequestBody MessagePageReqVO vo);

}
