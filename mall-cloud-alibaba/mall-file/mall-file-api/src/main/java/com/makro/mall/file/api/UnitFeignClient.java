package com.makro.mall.file.api;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.file.api.fallback.UnitFeignFallbackClient;
import com.makro.mall.file.pojo.entity.MmConfigUnit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description Unit Feign接口
 * @date 2021/11/29
 */
@FeignClient(value = "makro-file", fallback = UnitFeignFallbackClient.class, contextId = "unit-client")
public interface UnitFeignClient {

    @GetMapping("/api/v1/config/unit/{id}")
    BaseResponse<MmConfigUnit> detail(@PathVariable Long id);

    @GetMapping("/api/v1/config/unit/list")
    BaseResponse<List<MmConfigUnit>> list();

}
