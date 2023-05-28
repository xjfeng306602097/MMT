package com.makro.mall.file.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.file.api.PicFeignClient;
import com.makro.mall.file.pojo.dto.PicUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@Slf4j
public class PicFeignFallbackClient implements PicFeignClient {


    @Override
    public BaseResponse<PicUploadDTO> upload(MultipartFile file) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
