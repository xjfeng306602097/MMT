package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.OAuthClientFeignClient;
import com.makro.mall.admin.pojo.entity.SysOauthClient;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuthFeignFallbackClient implements OAuthClientFeignClient {

    @Override
    public BaseResponse<SysOauthClient> getOAuthClientById(String clientId) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
