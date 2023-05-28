package com.makro.mall.stat;

import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.api.MmBounceRateFeignClient;
import com.makro.mall.admin.api.MmMemberTypeFeignClient;
import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.product.api.ProdDataFeignClient;
import com.makro.mall.stat.pojo.api.AppFeignClient;
import com.makro.mall.template.api.TemplateFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/02/13
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {AppFeignClient.class, PublishFeignClient.class, MmActivityFeignClient.class,
        ProdDataFeignClient.class, MmMemberTypeFeignClient.class, MmBounceRateFeignClient.class, TemplateFeignClient.class})
public class StatCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatCollectorApplication.class, args);
    }

}
