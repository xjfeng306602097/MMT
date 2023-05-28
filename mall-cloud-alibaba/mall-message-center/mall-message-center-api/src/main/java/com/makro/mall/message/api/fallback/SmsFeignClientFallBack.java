package com.makro.mall.message.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.message.api.SmsFeignClient;
import com.makro.mall.message.dto.SmsMessagePageFeignDTO;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/5/7
 */
@Component
@Slf4j
public class SmsFeignClientFallBack implements SmsFeignClient {
    @Override
    public BaseResponse add(SmsMessage smsMessage) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<SmsMessagePageFeignDTO> smsUserPage(MessagePageReqVO vo) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
