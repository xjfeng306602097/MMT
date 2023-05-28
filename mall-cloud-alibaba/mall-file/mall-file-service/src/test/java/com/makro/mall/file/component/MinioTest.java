package com.makro.mall.file.component;

import com.makro.mall.file.CustomSpringbootTest;
import com.makro.mall.file.component.minio.MinioFileComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author xiaojunfeng
 * @description minio测试
 * @date 2022/7/28
 */
@CustomSpringbootTest
public class MinioTest {

    @Resource
    private MinioFileComponent minioFileComponent;


    @Test
    @DisplayName("模拟分片任务")
    void send() throws InterruptedException {

    }


}
