package com.makro.mall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/7/26 发送枚举
 */
@AllArgsConstructor
@Slf4j
public enum MessageSendEnum {
    NOT_SENT("0"),
    FAILED("1"),
    SUCCEEDED("2"),
    CANCELED("3"),
    REJECTED("10"),
    PARTIALLY_SUCCEEDED("5");

    @Getter
    private String status;


    public static MessageSendEnum getEnum(String status) {
        MessageSendEnum anEnum = null;
        for (MessageSendEnum item : values()) {
            if (item.getStatus().equals(status)) {
                anEnum = item;
                continue;
            }
        }
        return anEnum;
    }

}
