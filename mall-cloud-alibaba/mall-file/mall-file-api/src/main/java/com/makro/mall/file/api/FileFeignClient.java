package com.makro.mall.file.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.file.api.fallback.FileFeignFallbackClient;
import com.makro.mall.file.pojo.dto.StreamDTO;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author xiaojunfeng
 * @description 文件接口
 * @date 2021/10/26
 */
@FeignClient(value = "makro-file", fallback = FileFeignFallbackClient.class)
public interface FileFeignClient {

    /**
     * 文件流上传接口
     *
     * @return
     */
    @PostMapping(value = "/api/v1/files/stream", consumes = "application/json")
    BaseResponse<UploadResultDTO> uploadInputStream(@ApiParam("文件字节流") @RequestBody StreamDTO stream);

    /**
     * 文件删除接口
     *
     * @param path 上传后传入的文件路径
     * @return
     */
    @DeleteMapping("/api/v1/files")
    BaseResponse removeFile(@RequestParam String path);

    /**
     * 获取文件流接口
     *
     * @param bucketName 桶名称
     * @param objectName 文件名称
     * @return
     */
    @GetMapping(value = "/api/v1/files/stream")
    BaseResponse<byte[]> getObjectStream(@RequestParam String bucketName, @RequestParam String objectName);


}
