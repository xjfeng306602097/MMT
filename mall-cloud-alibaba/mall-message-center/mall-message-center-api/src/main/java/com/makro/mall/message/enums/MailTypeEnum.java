package com.makro.mall.message.enums;

import lombok.Getter;

/**
 * @author xiaojunfeng
 * @description 邮件类型
 * @date 2021/11/3
 */
public enum MailTypeEnum {

    /**
     * 枚举值
     */
    TEXT("text", "文本类型"),
    H5("h5", "h5类型"),
    H5_TEMPLATE("h5_template", "h5模板");

    @Getter
    private String value;

    @Getter
    private String label;

    MailTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static MailTypeEnum getByValue(String value) {
        MailTypeEnum mailTypeEnum = null;
        for (MailTypeEnum item : values()) {
            if (item.getValue().equals(value)) {
                mailTypeEnum = item;
                continue;
            }
        }
        return mailTypeEnum;
    }
}
