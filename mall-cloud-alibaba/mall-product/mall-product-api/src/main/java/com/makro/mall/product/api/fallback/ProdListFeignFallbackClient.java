package com.makro.mall.product.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.product.api.ProdListFeignClient;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ProdListFeignFallbackClient implements ProdListFeignClient {

    @Override
    public BaseResponse<Boolean> importData(String mmCode, ExcelDataFromSheetName excelData) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<Boolean> remove(String mmCode) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<Map<String, List<ProdList>>> getMapByMmCodes(Set<String> keySet) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
