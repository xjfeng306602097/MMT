package com.makro.mall.stat.pojo.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.pojo.api.fallback.ShortLinkFeignFallbackClient;
import com.makro.mall.stat.pojo.dto.ShortLinkGenerateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 功能描述: 短链生成接口
 *
 * @Author: xiaojunfeng
 * @Date: 2022/8/1 短链
 */
@FeignClient(value = "makro-stat", fallback = ShortLinkFeignFallbackClient.class, contextId = "makro-short-link")
public interface ShortLinkFeignClient {

    @PostMapping(value = "/api/v1/short-link")
    BaseResponse<String> shortLink(@RequestBody ShortLinkGenerateDTO dto);

    @GetMapping(value = "/api/v1/short-link")
    BaseResponse delShortLink(@RequestBody String shortLinkCode);

}
