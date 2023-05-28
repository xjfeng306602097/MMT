package com.makro.mall.job;

import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.stat.pojo.api.AppFeignClient;
import com.makro.mall.stat.pojo.api.AssemblyFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiaojunfeng
 * @description 任务调度用户服务应用
 * @date 2021/10/13
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {AppFeignClient.class, MmActivityFeignClient.class, AssemblyFeignClient.class})
@Slf4j
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
