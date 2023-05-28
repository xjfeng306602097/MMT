package com.makro.mall.admin.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class MmPublishJobTaskReqVO {
    private String id;

    @ApiModelProperty(value = "渠道 sms/line/email")
    private String channel;

    private String sendTo;
    @ApiModelProperty(value = "会员码")
    private String customerCode;
    @ApiModelProperty(value = "0-Not sent 1-Failed 2-Succeeded 3-Canceled")
    private String status;


}
