package com.makro.mall.product.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.product.api.ProdDataFeignClient;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class ProdDataFeignFallbackClient implements ProdDataFeignClient {

    @Override
    public BaseResponse<List<ProductVO>> listByIds(String ids) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<ProdStorage> get(String id) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<String> getMmCodeByItemCode(String itemCode) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
