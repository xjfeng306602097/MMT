package com.makro.mall.product.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.api.fallback.ProdListFeignFallbackClient;
import com.makro.mall.product.api.fallback.ProdStorageFeignFallbackClient;
import com.makro.mall.product.pojo.dto.ItemCodeSegmentDelDTO;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/22
 */
@FeignClient(value = "makro-product", fallback = ProdStorageFeignFallbackClient.class)
public interface ProdStorageFeignClient {

    String PROD_STORAGE_CLIENT = "/api/v1/prod/storage/client";

    @DeleteMapping(PROD_STORAGE_CLIENT + "/itemCodeSegment")
    BaseResponse<Boolean> itemCodeSegment(@RequestBody ItemCodeSegmentDelDTO dto);
}
