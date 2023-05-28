package com.makro.mall.admin.api.fallback;

import com.alibaba.fastjson2.JSONArray;
import com.makro.mall.admin.api.MmActivityExFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MmActivityExFeignFallbackClient implements MmActivityExFeignClient {
    @Override
    public BaseResponse<MakroPage<MmActivity>> getByCodes(JSONArray mmCodes, Long page, Long limit) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
