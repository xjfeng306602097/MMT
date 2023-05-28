package com.makro.mall.template.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.template.api.fallback.TemplateFeignFallbackClient;
import com.makro.mall.template.pojo.entity.MmTemplate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author xiaojunfeng
 * @description template接口
 * @date 2022/4/18
 */
@FeignClient(value = "makro-template", fallback = TemplateFeignFallbackClient.class)
public interface TemplateFeignClient {

    @PutMapping(value = "/api/v1/template/{code}/inner")
    BaseResponse updateUnlock(@PathVariable String code, @RequestBody MmTemplate template);

    @GetMapping("/api/v1/template/client/mm/{mmCode}")
    BaseResponse<MmTemplate> getByMmCode(@PathVariable String mmCode);

    @PostMapping("/api/v1/template/client/getByMmCodes")
    BaseResponse<Map<String, MmTemplate>> getByMmCodes(@RequestBody List<String> mmcodes);
}
