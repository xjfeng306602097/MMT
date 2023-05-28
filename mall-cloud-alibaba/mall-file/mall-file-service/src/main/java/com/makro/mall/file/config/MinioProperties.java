package com.makro.mall.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description minio配置相关
 * @date 2021/10/25
 */
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {

    private String endPoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private Map<String, String> bucketMap;

    public String getBucketName(String suffix) {
        return Optional.ofNullable(bucketMap.get(suffix)).orElse("default");
    }

    public String resolveSuffix(String filePath) {
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        suffix = suffix.indexOf("_") > 0 ? suffix.substring(0, suffix.indexOf("_")) : suffix;
        return suffix;
    }

    public String resolveBucketName(String filePath) {
        return getBucketName(resolveSuffix(filePath));
    }

    public String resolveObjectName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/"));
    }


}
