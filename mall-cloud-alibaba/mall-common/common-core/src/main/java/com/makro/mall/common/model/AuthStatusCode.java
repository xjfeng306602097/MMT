package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description admin 00
 * auth 01
 * @date 2022/5/12
 */
public class AuthStatusCode extends StatusCode {


    public static final StatusCode USERDETAILSSERVICE_CANNOT_BE_NULL;
    public static final StatusCode USERDETAILSSERVICE_MUST_BE_SET;

    static {
        USERDETAILSSERVICE_CANNOT_BE_NULL = new StatusCode("0101", "userDetailsService.cannot.be.null", Level.WARN);
        USERDETAILSSERVICE_MUST_BE_SET = new StatusCode("0102", "userDetailsService.must.be.set", Level.WARN);
    }

    public AuthStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }

    public AuthStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }
}
