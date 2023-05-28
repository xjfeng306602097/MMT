package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description admin 00
 * file 01
 * auth 02
 * message 03
 * product 04
 * Stat 05
 * template 06
 * @date 2022/5/12
 */
public class TemplateStatusCode extends StatusCode {


    public static final StatusCode CHECKTEMPLATELOCK_NEEDS_TO_SPECIFY_KEY;

    public static final StatusCode VERSION_IS_LOWER_THAN_DB;

    static {
        CHECKTEMPLATELOCK_NEEDS_TO_SPECIFY_KEY = new StatusCode("0601", "CheckTemplateLock.needs.to.specify.key", Level.WARN);
        VERSION_IS_LOWER_THAN_DB = new StatusCode("0602", "version.is.lower.than.db", Level.WARN);
    }

    public TemplateStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }

    public TemplateStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }
}
