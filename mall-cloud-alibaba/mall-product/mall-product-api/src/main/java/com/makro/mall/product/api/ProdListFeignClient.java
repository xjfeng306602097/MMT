package com.makro.mall.product.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.api.fallback.ProdListFeignFallbackClient;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/22
 */
@FeignClient(value = "makro-product", fallback = ProdListFeignFallbackClient.class)
public interface ProdListFeignClient {


    String PROD_LIST_CLIENT = "/api/v1/prod/list/client";

    @PostMapping(PROD_LIST_CLIENT + "/importData/{mmCode}")
    BaseResponse<Boolean> importData(@PathVariable String mmCode, @RequestBody ExcelDataFromSheetName excelData);

    @PostMapping(PROD_LIST_CLIENT + "/remove/{mmCode}")
    BaseResponse<Boolean> remove(@PathVariable String mmCode);

    @PostMapping(PROD_LIST_CLIENT + "/getMapByMmCodes")
    BaseResponse<Map<String, List<ProdList>>> getMapByMmCodes(@RequestBody Set<String> keySet);
}
