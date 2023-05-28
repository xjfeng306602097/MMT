package com.makro.mall.admin.api;


import com.makro.mall.admin.api.fallback.CustomerFeignFallbackClient;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = CustomerFeignFallbackClient.class, contextId = "customer-client")
public interface CustomerFeignClient {

    String API_V1_CUSTOMERS = "/api/v1/customers/client";

    @GetMapping(API_V1_CUSTOMERS + "/getByLineId")
    BaseResponse<Long> getByLineId(@RequestParam String lineUserId);

    @PostMapping(API_V1_CUSTOMERS + "/getByIds")
    BaseResponse<List<MmCustomer>> getByIds(@RequestBody List<String> cIds);

    @PostMapping(API_V1_CUSTOMERS + "/getVO")
    BaseResponse<MmCustomerVO> getVO(@RequestBody MmCustomer mmCustomer);
}
