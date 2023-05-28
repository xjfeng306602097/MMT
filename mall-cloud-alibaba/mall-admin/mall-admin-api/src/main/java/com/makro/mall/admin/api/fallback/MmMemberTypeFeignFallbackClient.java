package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.MmMemberTypeFeignClient;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MmMemberTypeFeignFallbackClient implements MmMemberTypeFeignClient {
    @Override
    public BaseResponse<List<MmMemberType>> list() {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
