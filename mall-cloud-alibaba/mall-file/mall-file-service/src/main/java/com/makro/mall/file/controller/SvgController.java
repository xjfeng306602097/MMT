package com.makro.mall.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.util.ByteConvertUtil;
import com.makro.mall.common.web.enums.MimeTypeEnum;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.config.MinioProperties;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/9/5
 */
@Api(tags = "svg上传")
@RestController
@RequestMapping("/api/v1/svg")
@RequiredArgsConstructor
@Slf4j
public class SvgController {

    private final MinioFileComponent minioFileComponent;

    private final MinioProperties minioProperties;

    @PostMapping
    @ApiOperation(value = "文件上传")
    public BaseResponse<UploadResultDTO> upload(
           @ApiParam("文件") @RequestParam(value = "file") String ins,
           @ApiParam("桶名称") @RequestParam(value = "bucketName", required = false) String bucketName,
           @ApiParam("object名称") @RequestParam(value = "objectName", required = false) String objectName
    ) {
        try {
            String suffix = "svg";
            String finalObjectName = Optional.ofNullable(objectName).orElse(IdUtil.simpleUUID() + "." + suffix);
            if (StrUtil.isEmpty(bucketName)) {
                bucketName = minioProperties.getBucketName(suffix);
            }
            byte[] bytes = ins.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            String path = minioFileComponent.putObjectWithProxy(bucketName, finalObjectName, inputStream, MimeTypeEnum.SVG.getMimeType());
            return BaseResponse.success(new UploadResultDTO().setPath(path).setMb(ByteConvertUtil.b2Mb(bytes.length)));
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return BaseResponse.error(e.getMessage());
        }
    }

}
