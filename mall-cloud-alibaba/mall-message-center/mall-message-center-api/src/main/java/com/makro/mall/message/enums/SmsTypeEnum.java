package com.makro.mall.message.enums;

import lombok.Getter;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/21
 */
public enum SmsTypeEnum {

    /**
     * GGG
     */
    GGG("GGG", "GGG短信平台");

    @Getter
    private String value;

    @Getter
    private String label;

    SmsTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static SmsTypeEnum getByValue(String value) {
        SmsTypeEnum smsEnum = null;
        for (SmsTypeEnum item : values()) {
            if (item.getValue().equals(value)) {
                smsEnum = item;
                continue;
            }
        }
        return smsEnum;
    }

}
