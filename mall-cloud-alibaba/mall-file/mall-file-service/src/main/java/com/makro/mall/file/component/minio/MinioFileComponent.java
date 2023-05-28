package com.makro.mall.file.component.minio;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.file.config.MinioProperties;
import com.makro.mall.file.util.CacheUtil;
import io.minio.*;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.ListPartsResult;
import io.minio.messages.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author xiaojunfeng
 * @description 处理文件上传下载删除等功能
 * @date 2021/10/25
 */
@Component
@Slf4j
public class MinioFileComponent implements InitializingBean {

    @Resource
    private MinioClient client;

    @Resource
    private ExtensionMinioClient extensionMinioClient;

    @Resource
    private MinioProperties minioProperties;

    @Value("${file.prefix:http://10.58.5.242:3344}")
    private String prefix;

    @Resource
    private CacheUtil cacheUtil;

    private Function<ObjectWriteResponse, String> urlConvertFunction;

    private boolean isAws;

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
        ObjectWriteResponse response = client.putObject(putObjectArgs);
        return this.urlConvertFunction.apply(response);
    }

    public String putObjectWithProxy(String bucketName, String objectName, InputStream inputStream, String contentType) {
        String url = putObject(bucketName, objectName, inputStream, contentType);
        log.info("upload success, url: {}", url);
        return replaceRegion(url);
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

    public CompletableFuture<Map<String, Object>> getUrls(String bucketName, String objName, int chunkNum) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, InvalidKeyException, InternalException {
        CompletableFuture<CreateMultipartUploadResponse> multipartUploadCompletableFuture = extensionMinioClient.createMultipartUploadAsync(bucketName, null, objName, null, null);
        return multipartUploadCompletableFuture.thenApply(multipartUploadResponse -> {
            Map<String, Object> resultMap = new HashMap<>(2, 1);
            String uploadId = multipartUploadResponse.result().uploadId();
            resultMap.put("uploadId", uploadId);
            //缓存 uploadId 对应 bucket 和 文件名
            cacheUtil.put(uploadId, bucketName + "|" + objName);
            List<String> urls = (List<String>) resultMap.computeIfAbsent("urls", key -> new ArrayList<>());
            log.info("开始下发url, bucketName: {} , name: {}, uploadId: [{}]", bucketName, objName, uploadId);

            for (int i = 1; i <= chunkNum; i++) {
                Map<String, String> extraQueryParams = new HashMap<>(2, 1);
                extraQueryParams.put("uploadId", uploadId);
                if (chunkNum != 1) {
                    //分块
                    extraQueryParams.put("partNumber", Integer.toString(i));
                }
                GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objName)
                        .extraQueryParams(extraQueryParams)
                        .expiry(10, TimeUnit.MINUTES)
                        .method(Method.PUT)
                        .build();
                try {
                    // 获取URL
                    String uploadUrl = extensionMinioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
                    log.info("下发url成功, uploadId: [{}], url: {}", uploadId, uploadUrl);
                    urls.add(uploadUrl);
                } catch (Exception e) {
                    log.error("获取分片链接失败", e);
                    throw new RuntimeException(e);
                }
            }
            return resultMap;
        });
    }

    public String aggMultipart(String uploadId) throws ExecutionException {
        String bucketAndName = (String) cacheUtil.get(uploadId);
        Assert.isTrue(bucketAndName != null, StatusCode.ILLEGAL_STATE);
        String[] array = bucketAndName.split("\\|");
        String bucketName = array[0];
        String objName = array[1];
        log.info("聚合 part bucketName: {} , name: {}, uploadId: [{}]", bucketAndName, objName, uploadId);
        try {
            CompletableFuture<ListPartsResponse> completableFuture = extensionMinioClient.listPartsAsync(bucketName, null, objName, 255, null, uploadId, null, null);
            return completableFuture.thenApply(listPartsResponse -> {
                ListPartsResult partsResult = listPartsResponse.result();
                Part[] parts = partsResult.partList().toArray(new Part[0]);
                try {
                    CompletableFuture<ObjectWriteResponse> responseCompletableFuture = extensionMinioClient.completeMultipartUploadAsync(bucketName, null, objName, uploadId, parts, null, null);
                    return responseCompletableFuture.thenApply(objectWriteResponse -> {
                        String etag = objectWriteResponse.etag();
                        String versionId = objectWriteResponse.versionId();
                        log.info("聚合完成 versionId: [{}], etag: {}", versionId, etag);
                        return objectWriteResponse.region();
                    }).exceptionally(throwable -> {
                        throw new RuntimeException(throwable);
                    }).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).exceptionally(throwable -> {
                throw new RuntimeException(throwable);
            }).get();
        } catch (Exception e) {
            log.error("聚合失败，part bucketName: {} , name: {}, uploadId: [{}]", bucketAndName, objName, uploadId, e);
            return null;
        }
    }

    public String resolveBucketName(String filePath) {
        return minioProperties.resolveBucketName(filePath);
    }

    public String resolveObjectName(String filePath) {
        return minioProperties.resolveObjectName(filePath);
    }

    public String resolveSuffix(String filePath) {
        return minioProperties.resolveSuffix(filePath);
    }

    public String replaceRegion(String region) {
        return isAws ? region : prefix + region.substring(region.indexOf("/", 10));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.isAws = minioProperties.getEndPoint().contains("amazonaws");
        if (this.isAws) {
            this.urlConvertFunction = this::convertAws;
        } else {
            this.urlConvertFunction = this::convertMinio;
        }
    }

    public String convertMinio(ObjectWriteResponse response) {
        String region = response.region();
        return StrUtil.isNotEmpty(region) ? region : minioProperties.getEndPoint() + "/" + response.bucket() + "/" + response.object();
    }

    public String convertAws(ObjectWriteResponse response) {
        StringBuffer stringBuffer = new StringBuffer(minioProperties.getEndPoint());
        stringBuffer.insert(3, minioProperties.getRegion() + ".");
        return "https://" + response.bucket() + "." + stringBuffer.toString() + "/" + response.object();
    }
}
