package com.makro.mall.admin.aspect.config;

import com.makro.mall.admin.aspect.RefreshRolePermissionAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaojunfeng
 * @description 刷新用户权限切面，触发相关接口刷新缓存信息
 * @date 2021/10/19
 */
@Configuration
public class RefreshRolePermissionConfig {

    @Bean
    protected RefreshRolePermissionAspect permissionAspect() {
        return new RefreshRolePermissionAspect();
    }

}
