package com.makro.mall.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.util.ByteConvertUtil;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.config.MinioProperties;
import com.makro.mall.file.pojo.dto.StreamDTO;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import io.minio.StatObjectResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description minio文件接口
 * @date 2021/10/25
 */
@Api(tags = "文件接口")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final MinioFileComponent minioFileComponent;
    private final MinioProperties minioProperties;

    @PostMapping
    @ApiOperation(value = "文件上传")
    public BaseResponse<UploadResultDTO> upload(
            @ApiParam("文件") @RequestParam(value = "file") MultipartFile file,
            @ApiParam("桶名称") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam("object名称") @RequestParam(value = "objectName", required = false) String objectName
    ) {
        try {
            String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String finalObjectName = Optional.ofNullable(objectName).orElse(IdUtil.simpleUUID() + "." + suffix);
            if (StrUtil.isEmpty(bucketName)) {
                bucketName = minioProperties.getBucketName(suffix);
            }
            String path = minioFileComponent.putObjectWithProxy(bucketName, finalObjectName, file.getInputStream(), file.getContentType());
            return BaseResponse.success(new UploadResultDTO().setPath(path).setMb(ByteConvertUtil.b2Mb(file.getSize())));
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return BaseResponse.error(e.getMessage());
        }
    }

    @PostMapping(value = "/stream", consumes = "application/json")
    @ApiOperation(value = "文件流上传")
    public BaseResponse<UploadResultDTO> uploadInputStream(@ApiParam("文件字节流") @RequestBody StreamDTO stream) {
        try {
            byte[] buf = stream.getBuf();
            String suffix = stream.getSuffix();
            String contentType = stream.getContentType();
            String objectName = stream.getObjectName();
            String bucketName = stream.getBucketName();
            InputStream ins = new ByteArrayInputStream(buf);
            String finalObjectName = Optional.ofNullable(objectName).orElse(IdUtil.simpleUUID() + "." + suffix);
            if (StrUtil.isEmpty(bucketName)) {
                bucketName = minioProperties.getBucketName(suffix);
            }
            String path = minioFileComponent.putObjectWithProxy(bucketName, finalObjectName, ins, contentType);
            return BaseResponse.success(new UploadResultDTO().setPath(path));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @DeleteMapping
    @ApiOperation(value = "文件删除")
    @SneakyThrows
    public BaseResponse removeFile(@ApiParam("文件路径") @RequestParam String path) {
        int lastIndex = path.lastIndexOf("/");
        String bucketName = path.substring(path.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
        String objectName = path.substring(lastIndex + 1);
        minioFileComponent.removeObject(bucketName, objectName);
        return BaseResponse.success();
    }

    @PostMapping(value = "/batchRemove", consumes = "multipart/form-data")
    @ApiOperation(value = "批量删除")
    public BaseResponse batchRemoveFile(
            @RequestParam(value = "paths") String paths
    ) throws Exception {
        List<String> pathList = Arrays.asList(paths.split(","));
        for (String path : pathList) {
            int lastIndex = path.lastIndexOf("/");
            String bucketName = path.substring(path.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
            String objectName = path.substring(lastIndex + 1);
            minioFileComponent.removeObject(bucketName, objectName);
        }
        return BaseResponse.success();
    }


    @GetMapping
    @ApiOperation(value = "文件下载,bucket不可直接访问时可通过该接口下载文件")
    public void download(HttpServletResponse response,@ApiParam("文件路径") @RequestParam String path) {
        InputStream in = null;
        try {
            int lastIndex = path.lastIndexOf("/");
            String bucketName = path.substring(path.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
            String objectName = path.substring(lastIndex + 1);
            StatObjectResponse stat = minioFileComponent.getObjectStat(bucketName, objectName);
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(objectName, "UTF-8"));
            in = minioFileComponent.getObject(bucketName, objectName);
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error("download file error", e);
            throw new BusinessException(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @GetMapping(value = "/stream")
    public BaseResponse<byte[]> getObjectStream(@RequestParam String bucketName, @RequestParam String objectName) throws IOException {
        return BaseResponse.success(minioFileComponent.getObject(bucketName, objectName).readAllBytes());
    }

}
