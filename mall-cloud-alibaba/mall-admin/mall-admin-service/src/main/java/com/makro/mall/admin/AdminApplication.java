package com.makro.mall.admin;

import com.makro.mall.common.redis.lock.annotation.EnableRedisLock;
import com.makro.mall.file.api.FileFeignClient;
import com.makro.mall.file.api.UnitFeignClient;
import com.makro.mall.message.api.MailMessageFeignClient;
import com.makro.mall.product.api.ProdDataFeignClient;
import com.makro.mall.stat.pojo.api.AppFeignClient;
import com.makro.mall.stat.pojo.api.ShortLinkFeignClient;
import com.makro.mall.template.api.TemplateFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiaojunfeng
 * @description 后台用户服务应用
 * @date 2021/10/13
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {FileFeignClient.class, AppFeignClient.class, UnitFeignClient.class, ShortLinkFeignClient.class, MailMessageFeignClient.class, ProdDataFeignClient.class, MailMessageFeignClient.class, TemplateFeignClient.class})
@EnableRedisLock
@EnableMongoAuditing
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
