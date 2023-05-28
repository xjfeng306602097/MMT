package com.makro.mall.product.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/12/14
 **/
@Data
@Configuration
@RefreshScope
public class NacosConfig {


    @Value("${excelimport.headRowNum}")
    private int headRowNum;

    @Value("${excelimport.requiredfield}")
    private List<Integer> requiredfield;

    @Bean
    @ConfigurationProperties(prefix = "excelimport.mapping")
    public Map<Integer, String> columns() {
        return new HashMap<>();
    }
}
