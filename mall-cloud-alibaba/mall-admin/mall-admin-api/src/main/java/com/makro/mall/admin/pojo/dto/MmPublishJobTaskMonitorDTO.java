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
public class MmPublishJobTaskMonitorDTO {

    private String id;
    @ApiModelProperty("MessageTaskEnum")
    private Long status;
    @ApiModelProperty("必传渠道 email/line/sms")
    private String channel;
}
