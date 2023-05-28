package com.makro.mall.file.config;

import com.makro.mall.file.component.minio.ExtensionMinioClient;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaojunfeng
 * @description minio配置类
 * @date 2021/10/25
 */
@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndPoint())
                .credentials(minioProperties.getAccessKey(),
                        minioProperties.getSecretKey())
                .region(minioProperties.getRegion()).build();
        return minioClient;
    }

    @Bean
    @ConditionalOnClass(value = ExtensionMinioClient.class)
    public ExtensionMinioClient extensionMinioClient() {
        MinioAsyncClient build = MinioAsyncClient.builder()
                .endpoint(minioProperties.getEndPoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .region(minioProperties.getRegion())
                .build();
        return new ExtensionMinioClient(build);
    }

}
