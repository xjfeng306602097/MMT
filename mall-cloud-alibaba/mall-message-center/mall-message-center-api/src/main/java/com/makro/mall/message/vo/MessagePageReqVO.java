package com.makro.mall.message.vo;

import com.makro.mall.common.enums.MessageSendEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagePageReqVO {
    private MessageSendEnum status;
    private Integer page;
    private Integer size;
    private String mmPublishJobTaskId;
    private String[] to;
}
