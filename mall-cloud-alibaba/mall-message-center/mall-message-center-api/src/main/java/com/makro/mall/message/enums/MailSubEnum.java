package com.makro.mall.message.enums;

import lombok.Getter;

/**
 * @author xiaojunfeng
 * @description 邮件类型
 * @date 2021/11/3
 */
public enum MailSubEnum {

    /**
     * 枚举值
     */
    SUBSCRIBE("SUBSCRIBE", "订阅"),
    UNSUBSCRIBE("UNSUBSCRIBE", "取消订阅");

    @Getter
    private String value;

    @Getter
    private String label;

    MailSubEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static MailSubEnum getByValue(String value) {
        MailSubEnum mailTypeEnum = null;
        for (MailSubEnum item : values()) {
            if (item.getValue().equals(value)) {
                mailTypeEnum = item;
                continue;
            }
        }
        return mailTypeEnum;
    }
}
