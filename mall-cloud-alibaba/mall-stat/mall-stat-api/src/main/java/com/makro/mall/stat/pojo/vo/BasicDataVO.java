package com.makro.mall.stat.pojo.vo;

import com.makro.mall.stat.pojo.dto.BasicDataDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 功能描述: 基础数据
 * 用户行为分析
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/13
 */
@Data
@Accessors(chain = true)
public class BasicDataVO {
    @ApiModelProperty(value = "访问次数")
    private BasicDataDTO pv;
    @ApiModelProperty(value = "访问人数")
    private BasicDataDTO uv;
    @ApiModelProperty(value = "商品点击次数")
    private BasicDataDTO productClick;
    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateTime;


}
