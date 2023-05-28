package com.makro.mall.message.pojo.dto;

import com.makro.mall.message.enums.SmsChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SmsMessageDTO {

    private String id;

    private SmsChannelEnum channelEnum;

    private List<String> receivers;

    private String msg;

    private String bizId;

    private Long mmPublishJobSmsTaskId;

}
