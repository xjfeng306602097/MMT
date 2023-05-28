package com.makro.mall.admin.api;


import com.makro.mall.admin.api.fallback.UserFeignFallbackClient;
import com.makro.mall.admin.pojo.dto.UserAuthDTO;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.common.model.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = UserFeignFallbackClient.class)
public interface UserFeignClient {

    @GetMapping("/api/v1/users/username/{username}")
    BaseResponse<UserAuthDTO> getUserByUsername(@PathVariable String username);


    @GetMapping("/api/v1/users/{id}")
    BaseResponse<SysUser> detail(@PathVariable String id);

    @PutMapping(value = "/api/v1/users/{id}/login/info")
    BaseResponse loginInfo(@PathVariable String id, @RequestBody SysUser user);

}
