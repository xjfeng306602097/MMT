package com.makro.mall.file.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.file.api.fallback.FileExFeignFallbackClient;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import feign.Headers;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaojunfeng
 * @description 文件接口
 * @date 2021/10/26
 */
@FeignClient(value = "makro-file", fallback = FileExFeignFallbackClient.class, contextId = "file-client", configuration = PicFeignClient.MultipartSupportConfig.class)
public interface FileExFeignClient {

    /**
     * 文件上传接口
     *
     * @param file
     * @param bucketName 桶名称，不指定的话，会根据文件后缀自动路由到对应的桶
     * @return
     */
    @Headers(value = {"Content-Type: multipart/form-data", "accept: application/json;charset=UTF-8"})
    @PostMapping(value = "/api/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseResponse<UploadResultDTO> upload(@RequestParam(value = "file") MultipartFile file,
                                         @RequestParam(value = "bucketName", required = false) String bucketName,
                                         @RequestParam(value = "objectName", required = false) String objectName);


    class MultipartSupportConfig {

        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }

}
