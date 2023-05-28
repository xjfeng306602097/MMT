package com.makro.mall.stat.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUvRequest {

    @ApiModelProperty(value = "万客隆会员号")
    protected String memberNo;

    @ApiModelProperty(value = "ip", hidden = true)
    protected String ip;

    @ApiModelProperty(value = "创建时间，自动生成", hidden = true)
    protected Date createTime = new Date();

    @ApiModelProperty(value = "ts，用于跟踪", hidden = true)
    private Long ts;
    private String uuid;
    private String channel;
}
