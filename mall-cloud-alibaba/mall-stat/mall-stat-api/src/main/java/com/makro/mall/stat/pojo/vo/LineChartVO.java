package com.makro.mall.stat.pojo.vo;

import com.makro.mall.stat.pojo.dto.MetadataDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述: 折线图VO
 * 用户行为分析
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/17
 */
@Data
public class LineChartVO {
    @ApiModelProperty("数据更新时间")
    private Date updateTime;
    @ApiModelProperty("折线图x轴")
    private List<String> label;
    @ApiModelProperty("折线图元数据")
    private List<MetadataDTO> list = new ArrayList<>();
}
