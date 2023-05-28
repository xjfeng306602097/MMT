package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * 功能描述:
 * admin 00
 * file 01
 * auth 02
 * message 03
 * product 04
 * Stat 05
 *
 * @Author: 卢嘉俊
 * @Date: 2022/7/7 用户行为分析
 */
public class StatStatusCode extends StatusCode {

    public static final StatusCode ONLY_DATA_WITHIN_THREE_MONTHS_SEARCHED;
    public static final StatusCode REPEATED_TRIGGER;
    public static final StatusCode INPUT_TIME_IS_NULL;

    static {
        ONLY_DATA_WITHIN_THREE_MONTHS_SEARCHED = new StatusCode("0501", "only.data.within.three.months.searched", Level.WARN);
        REPEATED_TRIGGER = new StatusCode("0502", "do.not.repeated.trigger", Level.WARN);
        INPUT_TIME_IS_NULL = new StatusCode("0503", "input.time.is.null", Level.WARN);
    }

    public StatStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }

    public StatStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }
}
