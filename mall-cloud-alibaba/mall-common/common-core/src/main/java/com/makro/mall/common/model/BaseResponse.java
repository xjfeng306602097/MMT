package com.makro.mall.common.model;

import com.makro.mall.common.util.MessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author xiaojunfeng
 * @description 基础返回体
 * @date 2021/10/9
 */
@ApiModel(
        value = "基础响应实体",
        description = "定义响应的基本字段"
)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Component
public class BaseResponse<T> {

    @Resource
    @ApiModelProperty(hidden = true)
    @Transient
    private MessageUtils messageUtils;

    @ApiModelProperty(hidden = true)
    private static BaseResponse instance;

    @PostConstruct
    public void init() {
        instance = this;
        instance.messageUtils = messageUtils;
    }

    @ApiModelProperty(
            value = "状态码",
            example = "0000"
    )
    private String code;
    @ApiModelProperty(
            value = "状态码描述",
            example = "请求成功"
    )
    private String msg;
    @ApiModelProperty("响应体")
    private T data;

    public boolean isSuccess() {
        return Objects.equals(StatusCode.SUCCESS.getCode(), this.code);
    }

    public static <U> BaseResponse<U> success() {
        return buildResponse(StatusCode.SUCCESS);
    }

    public static <U> BaseResponse<U> success(U data) {
        return buildResponse(StatusCode.SUCCESS, data);
    }

    public static <U> BaseResponse<U> success(String message) {
        return buildResponse(StatusCode.SUCCESS.message(message));
    }

    public static <U> BaseResponse<U> success(U data, String message) {
        return buildResponse(StatusCode.SUCCESS.message(message), data);
    }

    public static <U> BaseResponse<U> error() {
        return buildResponse(StatusCode.UNKNOWN_EXCEPTION);
    }

    public static <U> BaseResponse<U> error(U data) {
        return buildResponse(StatusCode.UNKNOWN_EXCEPTION, data);
    }

    public static <U> BaseResponse<U> error(String message) {
        return buildResponse(StatusCode.UNKNOWN_EXCEPTION.message(message));
    }

    public static <U> BaseResponse<U> judge(Boolean isSuccess) {
        return judge(isSuccess, (String) null);
    }

    public static <U> BaseResponse<U> judge(Boolean isSuccess, String message) {
        if (isSuccess) {
            return buildResponse(StatusCode.SUCCESS);
        }
        if (StringUtils.isEmpty(message)) {
            return buildResponse(StatusCode.BUSINESS_EXCEPTION);
        }
        return buildResponse(StatusCode.BUSINESS_EXCEPTION.message(message));

    }

    public static <U> BaseResponse<U> judge(Boolean isSuccess, StatusCode statusCode) {
        if (isSuccess) {
            return buildResponse(StatusCode.SUCCESS);
        }
        return buildResponse(statusCode);
    }

    public static <U> BaseResponse<U> error(StatusCode statusCode) {
        return buildResponse(statusCode);
    }

    public static <U> BaseResponse<U> error(StatusCode statusCode, String otherMsg) {
        return buildResponse(statusCode.message(statusCode.getMsg() + " " + otherMsg));
    }

    public static <U> BaseResponse<U> buildResponse(StatusCode statusCode) {
        BaseResponse<U> response = new BaseResponse<U>();
        response.setCode(statusCode.getCode());
        response.setMsg(statusCode.containsArgs() ? instance.messageUtils.get(statusCode.getMsg(), statusCode.getArgs())
                : instance.messageUtils.get(statusCode.getMsg()));
        return response;
    }

    public static <U> BaseResponse<U> buildResponse(StatusCode statusCode, U data) {
        BaseResponse<U> response = new BaseResponse<U>();
        response.setCode(statusCode.getCode());
        response.setMsg(statusCode.containsArgs() ? instance.messageUtils.get(statusCode.getMsg(), statusCode.getArgs())
                : instance.messageUtils.get(statusCode.getMsg()));
        response.setData(data);
        return response;
    }

}
