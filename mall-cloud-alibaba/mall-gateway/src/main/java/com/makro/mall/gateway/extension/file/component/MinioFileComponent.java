package com.makro.mall.gateway.extension.file.component;

import com.makro.mall.gateway.extension.file.config.MinioProperties;
import io.minio.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author xiaojunfeng
 * @description 处理文件上传下载删除等功能
 * @date 2021/10/25
 */
@Component
@Slf4j
public class MinioFileComponent {

    @Resource
    private MinioClient client;

    @Resource
    private MinioProperties minioProperties;

    @Value("${file.prefix:http://10.58.5.242:3344}")
    private String prefix;

    @SneakyThrows
    public void createBucketIfAbsent(String bucketName) {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();
        if (!client.bucketExists(bucketExistsArgs)) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucketName).build();
            client.makeBucket(makeBucketArgs);
        }
    }

    @SneakyThrows
    public String putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        createBucketIfAbsent(bucketName);
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .contentType(contentType)
                .stream(inputStream, inputStream.available(), -1)
                .build();
        return client.putObject(putObjectArgs).region();
    }

    public String putObjectWithProxy(String bucketName, String objectName, InputStream inputStream, String contentType) {
        String url = putObject(bucketName, objectName, inputStream, contentType);
        return prefix + url.substring(url.indexOf("/", 10));
    }

    @SneakyThrows
    public StatObjectResponse getObjectStat(String bucketName, String objectName) {
        return client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        return client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    public void removeObject(String bucketName, String objectName) throws Exception {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        client.removeObject(removeObjectArgs);
    }

}
