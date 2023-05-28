package com.makro.mall.admin.config;

import com.makro.mall.common.web.util.JwtUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description spring security配置，用于注入@CreateBy@LastModifiedBy
 * @date 2021/10/28
 */
@Component
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String username = JwtUtils.getUsername();
        if (username != null) {
            return Optional.of(username);
        } else {
            return Optional.empty();
        }
    }
}
