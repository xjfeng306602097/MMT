package com.makro.mall.product.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.api.fallback.ProdDataFeignFallbackClient;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.ProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/22
 */
@FeignClient(value = "makro-product", fallback = ProdDataFeignFallbackClient.class)
public interface ProdDataFeignClient {

    @GetMapping("/api/v1/product/data/list/{ids}")
    BaseResponse<List<ProductVO>> listByIds(@PathVariable String ids);

    @GetMapping("/api/v1/product/{id}")
    BaseResponse<ProdStorage> get(@PathVariable String id);

    @GetMapping("/api/v1/product/data/getMmCodeByItemCode/{itemCode}")
    BaseResponse<String> getMmCodeByItemCode(@PathVariable String itemCode);

}
