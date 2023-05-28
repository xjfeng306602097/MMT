package com.makro.mall.message.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/21
 */
public enum SmsChannelEnum {

    /**
     * GGG_MULTIPLE
     */
    @JsonProperty("GGG_MULTIPLE")
    GGG_MULTIPLE("GGG_MULTIPLE", "GGG发送多人渠道", SmsTypeEnum.GGG);

    @Getter
    private String value;

    @Getter
    private String label;

    @Getter
    private SmsTypeEnum type;

    SmsChannelEnum(String value, String label, SmsTypeEnum type) {
        this.value = value;
        this.label = label;
        this.type = type;
    }

    public static SmsChannelEnum getByValue(String value) {
        SmsChannelEnum channelEnum = null;
        for (SmsChannelEnum item : values()) {
            if (item.getValue().equals(value)) {
                channelEnum = item;
                continue;
            }
        }
        return channelEnum;
    }


}
