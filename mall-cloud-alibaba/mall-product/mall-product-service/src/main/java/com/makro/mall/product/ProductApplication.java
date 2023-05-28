package com.makro.mall.product;

import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.file.api.FileFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiaojunfeng
 * @description 后台用户服务应用
 * @date 2021/10/13
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {FileFeignClient.class, MmActivityFeignClient.class})
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
