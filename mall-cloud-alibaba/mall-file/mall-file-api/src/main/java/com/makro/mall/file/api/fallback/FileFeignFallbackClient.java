package com.makro.mall.file.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.file.api.FileFeignClient;
import com.makro.mall.file.pojo.dto.StreamDTO;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class FileFeignFallbackClient implements FileFeignClient {

    @Override
    public BaseResponse<UploadResultDTO> uploadInputStream(StreamDTO stream) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse removeFile(String path) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<byte[]> getObjectStream(String bucketName, String objectName) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
