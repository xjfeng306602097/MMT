package com.makro.mall.stat.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorDataDTO {
    @NotNull
    private Integer page;
    @NotNull
    private Integer limit;

    private Date startTime;
    private Date endTime;
    @ApiModelProperty("设备类型 IOS/Android/Windows/Mac/Linux/....")
    private String platform;
    @ApiModelProperty("进入渠道 email/sms/line/facebook/app")
    private String channel;
    @ApiModelProperty("IP地址")
    private String ip;
    private String mmCode;
}
