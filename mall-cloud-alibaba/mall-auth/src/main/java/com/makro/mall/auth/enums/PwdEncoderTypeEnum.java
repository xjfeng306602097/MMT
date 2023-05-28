package com.makro.mall.auth.enums;

import lombok.Getter;

/**
 * @author xiaojunfeng
 * @description 加密类型
 * @date 2021/10/11
 */
public enum PwdEncoderTypeEnum {

    /**
     * 加密方式
     */
    BCRYPT("{bcrypt}", "BCRYPT加密"),
    NOOP("{noop}", "无加密明文");

    @Getter
    private String prefix;

    PwdEncoderTypeEnum(String prefix, String desc) {
        this.prefix = prefix;
    }
}
