package com.makro.mall.stat.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class productAnalysisVO {

    @ApiModelProperty("英文名")
    private String nameEn;
    @ApiModelProperty("泰文")
    private String nameThai;
    @ApiModelProperty("商品编码")
    private String goodsCode;
    @ApiModelProperty("一个商品里的访客数总和")
    private Long visitors;
    @ApiModelProperty("一个商品里的访问量总和")
    private Long clicks;
}
