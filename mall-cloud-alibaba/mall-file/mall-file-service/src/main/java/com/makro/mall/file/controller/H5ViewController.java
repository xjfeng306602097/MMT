package com.makro.mall.file.controller;

import com.google.common.cache.*;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.config.MinioProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/8
 */
@RequiredArgsConstructor
@Slf4j
@Controller
@Api(tags ="H5View 直接展示文件")
@RequestMapping("/h5")
public class H5ViewController {

    private final MinioFileComponent minioFileComponent;

    private final MinioProperties minioProperties;

    private final LoadingCache<String, FastByteArrayOutputStream> CACHE = CacheBuilder.newBuilder()
            .maximumSize(50)
            .concurrencyLevel(8)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    log.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                }
            })
            .build(new CacheLoader<String, FastByteArrayOutputStream>() {
                @Override
                public FastByteArrayOutputStream load(String s) throws Exception {
                    String[] array = s.split(",");
                    String bucketName = array[0];
                    String filename = array[1];
                    InputStream response = minioFileComponent.getObject(bucketName, filename);
                    byte[] buf = new byte[1024];
                    int len;
                    try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                        while ((len = response.read(buf)) != -1) {
                            os.write(buf, 0, len);
                        }
                        os.flush();
                        return os;
                    }
                }
            });


    @ApiOperation("直接展示文件")
    @GetMapping("/{filename}")
    public void view(@PathVariable String filename, HttpServletResponse res) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        String bucketName = minioProperties.getBucketName(suffix);
        try (FastByteArrayOutputStream os = CACHE.get(bucketName + "," + filename)) {
            byte[] bytes = os.toByteArray();
            res.setCharacterEncoding("utf-8");
            res.setContentType("text/html");
            try (ServletOutputStream stream = res.getOutputStream()) {
                stream.write(bytes);
                stream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
