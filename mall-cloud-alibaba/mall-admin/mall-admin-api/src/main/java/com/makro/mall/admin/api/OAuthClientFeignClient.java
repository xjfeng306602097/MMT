package com.makro.mall.admin.api;

import com.makro.mall.admin.api.fallback.OAuthFeignFallbackClient;
import com.makro.mall.admin.pojo.entity.SysOauthClient;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author xiaojunfeng
 * @description OAuthClient Feuign
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = OAuthFeignFallbackClient.class, contextId = "oauth-client")
public interface OAuthClientFeignClient {

    @GetMapping("/api/v1/oauth-clients/{clientId}")
    BaseResponse<SysOauthClient> getOAuthClientById(@PathVariable String clientId);
}
