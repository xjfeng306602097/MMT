package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.MmMemberTypeFeignFallbackClient;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = MmMemberTypeFeignFallbackClient.class, contextId = "mm-member-type")
public interface MmMemberTypeFeignClient {

    @GetMapping(value = "/api/v1/memberType/list")
    BaseResponse<List<MmMemberType>> list();

}
