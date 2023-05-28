package com.makro.mall.common.enums;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/9
 */
public enum HealthCheckStatusEnum {

    /**
     *
     */
    NONE(2, "无此组件"),
    NORMAL(1, "正常"),
    ABNORMAL(0, "不正常");

    private final int status;
    private final String desc;

    private HealthCheckStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return this.status;
    }

    public String getDesc() {
        return this.desc;
    }

}
