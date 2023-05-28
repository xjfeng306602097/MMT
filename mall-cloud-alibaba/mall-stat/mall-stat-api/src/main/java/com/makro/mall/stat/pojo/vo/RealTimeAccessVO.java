package com.makro.mall.stat.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RealTimeAccessVO {
    @ApiModelProperty("实时访问次数")
    LineChartVO visits;
    @ApiModelProperty("实时访问人数")
    LineChartVO visitors;
}
