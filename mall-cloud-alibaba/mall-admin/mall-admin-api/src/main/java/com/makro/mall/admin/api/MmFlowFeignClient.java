package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.MmFlowFeignFallbackClient;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = MmFlowFeignFallbackClient.class, contextId = "mm-flow-client")
public interface MmFlowFeignClient {

    String FLOW = "/api/v1/flow/client";

    @PutMapping(value = FLOW + "/rollback")
    BaseResponse rollback(@RequestBody String code);

}
