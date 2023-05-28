package com.makro.mall.template.aspect.config;

import com.makro.mall.template.aspect.CheckTemplateLockAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaojunfeng
 * @description 刷新用户权限切面，触发相关接口刷新缓存信息
 * @date 2021/10/19
 */
@Configuration
public class CheckTemplateLockConfig {

    @Bean
    protected CheckTemplateLockAspect permissionAspect() {
        return new CheckTemplateLockAspect();
    }

}
