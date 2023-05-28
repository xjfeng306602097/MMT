package com.makro.mall.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证方式枚举
 *
 * @author xianrui
 * @date 2021/10/4
 */
public enum I18nEnum {

    /**
     * 枚举值
     */
    ZH_CN("zh_CN", "简体中文"),
    EN_US("en_US", "英文");

    @Getter
    private final String value;

    @Getter
    private final String label;

    I18nEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static I18nEnum getByValue(String value) {
        I18nEnum i18nEnum = null;
        for (I18nEnum item : values()) {
            if (item.getValue().equals(value)) {
                i18nEnum = item;
                break;
            }
        }
        return i18nEnum;
    }

    public static List<String> getAllValues() {
        return Arrays.stream(values()).map(I18nEnum::getValue).collect(Collectors.toList());
    }

}
