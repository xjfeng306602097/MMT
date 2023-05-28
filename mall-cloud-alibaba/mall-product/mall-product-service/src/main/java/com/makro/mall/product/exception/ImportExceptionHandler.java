package com.makro.mall.product.exception;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/12/14
 **/
@RestControllerAdvice
@Slf4j
public class ImportExceptionHandler {
    @ExceptionHandler(SizeLimitExceededException.class)
    public BaseResponse<String> sizeLimitExceeded(SizeLimitExceededException e) {
        return BaseResponse.error(StatusCode.FILE_SIZE_LIMIT);
    }
}
