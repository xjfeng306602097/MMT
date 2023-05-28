package com.makro.mall.message;

import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.common.redis.lock.annotation.EnableRedisLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiaojunfeng
 * @description 消息服务
 * @date 2021/10/13
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {CustomerFeignClient.class, MmActivityFeignClient.class})
@EnableMongoAuditing
@EnableRedisLock
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
