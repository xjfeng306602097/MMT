package com.makro.mall.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.FileStatusCode;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/28
 */
@RestController
@RequestMapping("/api/v1/files/upload")
@AllArgsConstructor
@Api(tags = "分片上传接口")
public class FileUploadController {

    private final MinioFileComponent minioFileComponent;

    /**
     * 基本分片字节单位(5m)
     */
    private static final int FIX_FILE_BASE_UNIT = 5242880;

    private static final int MAX_LIMIT_SIZE = 1024 * 1024 * 1024;

    @GetMapping("/urls")
    @ApiOperation(value = "获取分片上传链接")
    @CrossOrigin
    public BaseResponse<Map<String, Object>> getUrls(@RequestParam Long fileSize, @RequestParam String fileName, @RequestParam(required = false) String bucketName) throws Exception {
        int chunkNum = getChunkNum(fileSize);
        Assert.isTrue(fileSize < MAX_LIMIT_SIZE, FileStatusCode.ABOVE_MAX_UPLOAD_SIZE.args("1Gb"));
        String suffix = minioFileComponent.resolveSuffix(fileName);
        CompletableFuture<Map<String, Object>> completableFuture = minioFileComponent
                .getUrls(StrUtil.isNotEmpty(bucketName) ? bucketName : minioFileComponent.resolveBucketName(fileName),
                        IdUtil.simpleUUID() + "." + suffix, chunkNum);
        Map<String, Object> map = completableFuture.get();
        //返回分块大小供客户端分块,方便扩展分块规则
        map.put("chunkSize", FIX_FILE_BASE_UNIT);
        return BaseResponse.success(map);
    }

    /**
     * 暂时固定按照 5m 为单位拆分, 可拓展
     * 获取分片数
     * egg: 16m -> 5m + 5m + 6m ： chunkNum = 3
     * egg: 3m -> 3m ： chunkNum = 1
     *
     * @param fileSize 文件字节数
     * @return chunkNum
     */
    private int getChunkNum(long fileSize) {
        return fileSize <= FIX_FILE_BASE_UNIT ? 1 : (int) (fileSize / FIX_FILE_BASE_UNIT);
    }

    /**
     * 聚合多个块
     *
     * @return
     */
    @GetMapping("agg")
    @ApiOperation(value = "分片链接都触发完毕后，触发合并操作")
    @CrossOrigin
    public BaseResponse<UploadResultDTO> aggregate(@RequestParam String uploadId) throws ExecutionException {
        String region = minioFileComponent.aggMultipart(uploadId);
        return BaseResponse.success(new UploadResultDTO().setPath(minioFileComponent.replaceRegion(region)));
    }

}
