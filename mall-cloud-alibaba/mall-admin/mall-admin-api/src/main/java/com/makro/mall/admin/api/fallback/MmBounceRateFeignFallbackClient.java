package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.MmBounceRateFeignClient;
import com.makro.mall.admin.pojo.entity.MmBounceRate;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 */
@Component
@Slf4j
public class MmBounceRateFeignFallbackClient implements MmBounceRateFeignClient {

    @Override
    public BaseResponse<MmBounceRate> getByMmCode(String mmCode) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
