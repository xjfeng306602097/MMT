package com.makro.mall.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiaojunfeng
 * @description 业务类型
 * @date 2021/10/10
 */
public enum BusinessTypeEnum {

    /**
     *
     */
    ADMIN_USER("admin_user", 100),
    USER("user", 200);

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private Integer value;

    BusinessTypeEnum(String code, Integer value) {
        this.code = code;
        this.value = value;
    }

    public static BusinessTypeEnum getValue(String code) {
        BusinessTypeEnum businessTypeEnum = null;
        for (BusinessTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                businessTypeEnum = value;
                break;
            }
        }
        return businessTypeEnum;
    }
}
