package com.makro.mall.common.enums;

import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 0-发布失败,1-未发布,2-发布中,3-发布成功,4-发布部分成功
 */
@AllArgsConstructor
@Slf4j
public enum MessageTaskEnum {
    NOT_SENT(0L),
    PUSHING(1L),
    FAILED(2L),
    PARTIALLY_SUCCEEDED(3L),
    SUCCEEDED(4L),
    CANCELED(5L);

    @Getter
    private Long status;


    public static MessageTaskEnum getEnum(Long status) {
        for (MessageTaskEnum item : values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        log.error("getEnum status:{}",status);
        throw new BusinessException(StatusCode.BUSINESS_EXCEPTION);
    }


    public static MessageSendEnum getMessageSendEnum(Long value) {
        switch (MessageTaskEnum.getEnum(value)) {

            case FAILED:
                return MessageSendEnum.FAILED;
            case NOT_SENT:
                return MessageSendEnum.NOT_SENT;
            case PUSHING:
                return MessageSendEnum.NOT_SENT;
            case SUCCEEDED:
                return MessageSendEnum.SUCCEEDED;
            case PARTIALLY_SUCCEEDED:
                return MessageSendEnum.PARTIALLY_SUCCEEDED;
            default:
                return MessageSendEnum.FAILED;
        }
    }
}
