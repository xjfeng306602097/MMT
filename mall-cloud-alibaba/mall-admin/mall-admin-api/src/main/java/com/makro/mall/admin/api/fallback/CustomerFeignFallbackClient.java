package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CustomerFeignFallbackClient implements CustomerFeignClient {


    @Override
    public BaseResponse<Long> getByLineId(String lineUserId) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<List<MmCustomer>> getByIds(List<String> cIds) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<MmCustomerVO> getVO(MmCustomer mmCustomer) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

}
