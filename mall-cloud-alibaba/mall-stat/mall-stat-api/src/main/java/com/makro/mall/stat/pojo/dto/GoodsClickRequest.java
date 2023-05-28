package com.makro.mall.stat.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author xiaojunfeng
 * @description 商品点击请求
 * @date 2022/2/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GoodsClickRequest extends ClickRequest {

    @ApiModelProperty(value = "商品编码")
    @NotBlank
    private String goodsCode;

    @ApiModelProperty(value = "页码")
    private String pageNo;

    @ApiModelProperty(value = "商品类型")
    private String goodsType;

    @ApiModelProperty(value = "页面类型")
    private String pageType;

}
