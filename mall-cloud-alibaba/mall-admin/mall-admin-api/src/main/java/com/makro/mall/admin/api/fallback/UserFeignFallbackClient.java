package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.UserFeignClient;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class UserFeignFallbackClient implements UserFeignClient {

    @Override
    public BaseResponse getUserByUsername(String username) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<SysUser> detail(String id) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse loginInfo(String id, SysUser user) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
