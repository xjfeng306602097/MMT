package com.makro.mall.stat.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarChartVO {
    @ApiModelProperty("x轴名称")
    private String name;
    @ApiModelProperty("总数")
    private Long total;
    private Long value;
}
