package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.MmFlowFeignClient;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MmFlowFeignFallbackClient implements MmFlowFeignClient {

    @Override
    public BaseResponse rollback(String code) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
