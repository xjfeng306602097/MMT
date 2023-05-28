package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.MmSegmentFeignClient;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MmSegmentFeignFallbackClient implements MmSegmentFeignClient {
    @Override
    public BaseResponse<Boolean> invalidSegmentHandler() {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<Long> getIdIfNullCreateThat(String segmentName) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
