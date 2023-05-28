package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.MmActivityFeignFallbackClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = MmActivityFeignFallbackClient.class, contextId = "mm-activity-client")
public interface MmActivityFeignClient {

    @PutMapping(value = "/api/v1/activity/code/{code}")
    BaseResponse updateByCode(@PathVariable String code, @Validated @RequestBody MmActivity activity);

    @PutMapping(value = "/clear-template")
    BaseResponse clearTemplate(List<String> code);

    @GetMapping(value = "/api/v1/activity/client/mmCode/{mmCode}")
    BaseResponse<MmActivity> getByCode(@PathVariable String mmCode);

    @GetMapping(value = "/api/v1/client/activity/getPublishUrlByCode")
    BaseResponse<MmActivity> getPublishUrlByCode(@RequestParam String mmCode);

    @GetMapping(value = "/api/v1/activity/scanMmActivityForFailure")
    BaseResponse<Boolean> scanMmActivityForFailure();

    @GetMapping(value = "/api/v1/client/activity/listMmCodeByStatus")
    BaseResponse<List<MmActivity>> listMmCodeByStatus(@RequestParam Long status);
}
