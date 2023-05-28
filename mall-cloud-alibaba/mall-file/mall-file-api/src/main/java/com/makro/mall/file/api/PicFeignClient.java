package com.makro.mall.file.api;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.file.api.fallback.PicFeignFallbackClient;
import com.makro.mall.file.pojo.dto.PicUploadDTO;
import feign.Headers;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaojunfeng
 * @description 图片上传feign接口
 * @date 2022/3/23
 */
@FeignClient(value = "makro-file", fallback = PicFeignFallbackClient.class, contextId = "pic-client", configuration = PicFeignClient.MultipartSupportConfig.class)
public interface PicFeignClient {

    /**
     * 文件流上传接口
     *
     * @return
     */
    @Headers(value = {"Content-Type: multipart/form-data", "accept: application/json;charset=UTF-8"})
    @PostMapping(value = "/api/v1/pictures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseResponse<PicUploadDTO> upload(@RequestPart("file") MultipartFile file);

    class MultipartSupportConfig {

        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }

}
