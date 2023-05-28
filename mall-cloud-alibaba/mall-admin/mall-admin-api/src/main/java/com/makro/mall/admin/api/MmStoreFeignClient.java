package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.MmStoreFeignFallbackClient;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "makro-admin", fallback = MmStoreFeignFallbackClient.class, contextId = "mm-store-client")
public interface MmStoreFeignClient {
    String STORE = "/api/v1/stores/client";

    @GetMapping(value = STORE + "/syncFromMakro")
    BaseResponse<Boolean> syncFromMakro();

}
