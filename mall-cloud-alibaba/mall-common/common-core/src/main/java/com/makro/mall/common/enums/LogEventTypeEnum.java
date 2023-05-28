package com.makro.mall.common.enums;

import lombok.Getter;

/**
 * 认证方式枚举
 *
 * @author xianrui
 * @date 2021/10/4
 */
public enum LogEventTypeEnum {

    /**
     * 枚举值
     */
    GOODS_CLICK_TYPE("0001", "goods_click"),
    PAGE_VIEW_TYPE("0002", "page_view"),
    PAGE_STAY_TYPE("0003", "page_stay"),
    APP_UV_TYPE("0004", "app_uv");

    @Getter
    private final String code;

    @Getter
    private final String name;

    LogEventTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LogEventTypeEnum getNameByCode(String code) {
        LogEventTypeEnum authenticationMethodEnum = null;
        for (LogEventTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                authenticationMethodEnum = item;
                break;
            }
        }
        return authenticationMethodEnum;
    }

}
