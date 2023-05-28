package com.makro.mall.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.config.MinioProperties;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/8
 */
@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/v1/files/h5")
@Api(tags = "h5文件上传")
public class H5Controller {

    private final MinioFileComponent minioFileComponent;
    private final MinioProperties minioProperties;

    @Value("${h5.prefix:https://dev-gateway-api.makrogo.com/makro-file/h5/}")
    private String prefix;

    @PostMapping
    @ApiOperation(value = "h5文件上传")
    @ResponseBody
    public BaseResponse<UploadResultDTO> upload(
            @ApiParam("文件") @RequestParam(value = "file") MultipartFile file,
            @ApiParam("桶名称") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam("object名称") @RequestParam(value = "objectName", required = false) String objectName
    ) {
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String finalObjectName = Optional.ofNullable(objectName).orElse(IdUtil.simpleUUID() + "." + suffix);
            if (StrUtil.isEmpty(bucketName)) {
                bucketName = minioProperties.getBucketName(suffix);
            }
//            if (StrUtil.isNotBlank(objectName)) {
//                minioFileComponent.removeObject(bucketName,objectName);
//            }
            String path = minioFileComponent.putObject(bucketName, finalObjectName, file.getInputStream(), file.getContentType());
            log.info("Upload success, path is :{}", path);
            return BaseResponse.success(new UploadResultDTO().setPath(prefix + finalObjectName));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

}
