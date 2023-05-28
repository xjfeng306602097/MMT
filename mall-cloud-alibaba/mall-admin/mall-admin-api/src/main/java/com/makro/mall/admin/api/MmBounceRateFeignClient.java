package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.MmBounceRateFeignFallbackClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmBounceRate;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = MmBounceRateFeignFallbackClient.class, contextId = "mm-bounceRate-client")
public interface MmBounceRateFeignClient {

    @GetMapping(value = "/api/v1/mm/bounceRate/{mmCode}")
    BaseResponse<MmBounceRate> getByMmCode(@PathVariable String mmCode);

}
