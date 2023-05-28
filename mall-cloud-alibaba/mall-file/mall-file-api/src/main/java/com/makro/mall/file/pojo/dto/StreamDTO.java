package com.makro.mall.file.pojo.dto;

import lombok.Data;

/**
 * @Description: 包装文件字节流及其他文件上传所需信息
 * @Author: zhuangzikai
 * @Date: 2021/11/8
 **/
@Data
public class StreamDTO {
    private byte[] buf;
    private String suffix;
    private String contentType;
    private String bucketName;
    private String objectName;

    public StreamDTO(byte[] buf, String suffix, String contentType, String bucketName, String objectName) {
        this.buf = buf;
        this.suffix = suffix;
        this.contentType = contentType;
        this.bucketName = bucketName;
        this.objectName = objectName;
    }

    public StreamDTO() {

    }
}
