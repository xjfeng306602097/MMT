package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.MmSegmentFeignFallbackClient;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Repeatable;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = MmSegmentFeignFallbackClient.class, contextId = "mm-segment-client")
public interface MmSegmentFeignClient {

    String SEGMENTS = "/api/v1/segments";

    @GetMapping(value = SEGMENTS + "/invalidSegmentHandler")
    BaseResponse<Boolean> invalidSegmentHandler();

    @GetMapping(value = SEGMENTS + "/getIdIfNullCreateThat")
    BaseResponse<Long> getIdIfNullCreateThat(@RequestParam String segmentName);
}
