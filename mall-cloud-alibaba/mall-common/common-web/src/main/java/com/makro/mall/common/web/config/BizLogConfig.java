package com.makro.mall.common.web.config;

import com.makro.mall.common.web.aspect.BizLogInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jincheng
 */
@Configuration
@Slf4j
public class BizLogConfig {
    @Bean
    protected BizLogInterceptor cacheAspect() {
        return new BizLogInterceptor();
    }
}
