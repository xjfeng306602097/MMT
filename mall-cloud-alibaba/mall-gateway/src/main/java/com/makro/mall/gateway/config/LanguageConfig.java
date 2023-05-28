package com.makro.mall.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/8/11
 */
@Configuration
public class LanguageConfig {

    @Bean
    public GlobalFilter languageFilter(ApplicationContext applicationContext) {
        return new LanguageFilter(applicationContext);
    }

}
