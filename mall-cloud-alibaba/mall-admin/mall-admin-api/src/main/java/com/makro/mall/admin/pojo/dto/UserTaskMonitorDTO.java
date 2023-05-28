package com.makro.mall.admin.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserTaskMonitorDTO {

    private String taskId;
    @ApiModelProperty("MessageSendEnum")
    private String status;
    @ApiModelProperty("必传渠道 email/line/sms")
    private String channel;
    private String sendTo;
    private List<String> sendToForLine;
}
