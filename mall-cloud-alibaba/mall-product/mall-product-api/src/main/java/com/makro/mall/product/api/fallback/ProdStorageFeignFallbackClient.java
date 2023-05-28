package com.makro.mall.product.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.product.api.ProdStorageFeignClient;
import com.makro.mall.product.pojo.dto.ItemCodeSegmentDelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProdStorageFeignFallbackClient implements ProdStorageFeignClient {
    @Override
    public BaseResponse<Boolean> itemCodeSegment(ItemCodeSegmentDelDTO dto) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
