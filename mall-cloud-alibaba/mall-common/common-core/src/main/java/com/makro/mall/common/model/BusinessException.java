package com.makro.mall.common.model;


/**
 * @author xiaojunfeng
 * @description 业务异常处理
 * @date 2021/10/9
 */
public class BusinessException extends RuntimeException {

    private transient StatusCode code;
    private transient boolean logStacktrace;

    public BusinessException() {
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
    }

    public BusinessException(String message) {
        super(message);
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
        this.code = this.code.message(message);
    }

    public BusinessException(StatusCode code) {
        super(code.getMsg());
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
        this.code = code;
    }

    public BusinessException(String message, StatusCode code) {
        super(message);
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
        this.code = code.message(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
        this.code.message(message);
    }

    public BusinessException(String message, Throwable cause, StatusCode code) {
        super(message, cause);
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
        this.code = code.message(message);
    }

    public BusinessException(Throwable cause, StatusCode code) {
        super(cause);
        this.code = StatusCode.UNKNOWN_EXCEPTION;
        this.logStacktrace = true;
        this.code = code;
    }

    public BusinessException withLogStackTrace(boolean logStacktrace) {
        this.logStacktrace = logStacktrace;
        return this;
    }

    public StatusCode getCode() {
        return this.code;
    }

    public boolean isLogStacktrace() {
        return this.logStacktrace;
    }

}
