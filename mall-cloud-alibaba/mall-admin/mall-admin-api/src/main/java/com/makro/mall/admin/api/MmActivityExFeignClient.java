package com.makro.mall.admin.api;

import com.alibaba.fastjson2.JSONArray;
import com.makro.mall.admin.api.fallback.MmActivityExFeignFallbackClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = MmActivityExFeignFallbackClient.class, contextId = "mm-activity-ex-client")
public interface MmActivityExFeignClient {


    @GetMapping("/api/v1/activity/mmCodes/{mmCodes}")
    BaseResponse<MakroPage<MmActivity>> getByCodes(@PathVariable JSONArray mmCodes, @RequestParam Long page, @RequestParam Long limit);
}
