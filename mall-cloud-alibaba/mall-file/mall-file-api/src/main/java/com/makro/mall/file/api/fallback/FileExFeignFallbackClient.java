package com.makro.mall.file.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.file.api.FileExFeignClient;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@Slf4j
public class FileExFeignFallbackClient implements FileExFeignClient {

    @Override
    public BaseResponse<UploadResultDTO> upload(MultipartFile file, String bucketName, String objectName) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
