package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.util.ByteConvertUtil;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.pojo.dto.FileZipDTO;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author xiaojunfeng
 * @description 将文件打包成zip并上传到minio
 * @date 2022/4/12
 */
@Api(tags = "文件上传打包zip接口")
@RestController
@RequestMapping("/api/v1/zip/files")
@RequiredArgsConstructor
@Slf4j
public class ZipController {

    private final MinioFileComponent minioFileComponent;

    private static final Integer BUFFER_SIZE = 1024;

    @PostMapping
    @ApiOperation(value = "文件上传")
    public BaseResponse<UploadResultDTO> upload(@RequestBody FileZipDTO zipDTO) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.setMethod(ZipOutputStream.DEFLATED);
        InputStream inputStream = null;
        try {
            List<FileZipDTO.FileEntry> files = zipDTO.getFiles();
            // 解析文件url对应的bucket-name和文件名
            for (FileZipDTO.FileEntry file : files) {
                long begin = System.currentTimeMillis();
                String fileName = file.getFile().substring(file.getFile().lastIndexOf("/"));
                String bucketName = minioFileComponent.resolveBucketName(fileName);
                inputStream = minioFileComponent.getObject(bucketName, fileName);
                zos.putNextEntry(new ZipEntry(file.getFilePath()));
                int len;
                byte[] buf = new byte[BUFFER_SIZE];
                while ((len = inputStream.read(buf)) >= 0) {
                    zos.write(buf, 0, len);
                    zos.flush();
                }
                inputStream.close();
                zos.closeEntry();
                log.info("处理文件：{}耗费时间：{}", fileName, System.currentTimeMillis() - begin);
            }
            zos.flush();
            zos.close();
            baos.close();
            String path = minioFileComponent.putObjectWithProxy("makro-zip",
                    StrUtil.isEmpty(zipDTO.getZipName()) ? UUID.randomUUID() + ".zip" : zipDTO.getZipName(),
                    new ByteArrayInputStream(baos.toByteArray()), "application/x-zip-compressed");
            return BaseResponse.success(new UploadResultDTO().setPath(path).setMb(ByteConvertUtil.b2Mb(baos.toByteArray().length)));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        } finally {
            zos.close();
            baos.close();
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
