package com.makro.mall.common.web.exception;

import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * @author xiaojunfeng
 * @description 统一异常处理器
 * @date 2021/10/10
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 表单绑定到 java bean 出错时抛出 BindException 异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public <T> BaseResponse<T> processException(BindException e) {
        log.error(e.getMessage(), e);
        JSONObject msg = new JSONObject();
        e.getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                msg.put(fieldError.getField(),
                        fieldError.getDefaultMessage());
            } else {
                msg.put(error.getObjectName(),
                        error.getDefaultMessage());
            }
        });
        return BaseResponse.error(StatusCode.REQUEST_PARAM_ILLEGAL.message(msg.toJSONString()));
    }

    /**
     * 普通参数(非 java bean)校验出错时抛出 ConstraintViolationException 异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public <T> BaseResponse<T> processException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        JSONObject msg = new JSONObject();
        e.getConstraintViolations().forEach(constraintViolation -> {
            String template = constraintViolation.getMessage();
            String path = constraintViolation.getPropertyPath().toString();
            msg.put(path, template);
        });
        return BaseResponse.error(StatusCode.REQUEST_PARAM_ILLEGAL.message(msg.toJSONString()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public <T> BaseResponse<T> processException(ValidationException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(StatusCode.REQUEST_PARAM_ILLEGAL.message("参数校验非法"));
    }

    /**
     * NoHandlerFoundException
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> BaseResponse<T> processException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(StatusCode.NOT_FOUND);
    }

    /**
     * MissingServletRequestParameterException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> BaseResponse<T> processException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(StatusCode.PARAM_IS_NULL);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public <T> BaseResponse<T> handleBizException(BusinessException e) {
        log.error("业务异常，异常原因：{}", e.getMessage(), e);
        if (e.getCode() != null) {
            return BaseResponse.error(e.getCode());
        }
        return BaseResponse.error(e.getMessage());
    }

}
