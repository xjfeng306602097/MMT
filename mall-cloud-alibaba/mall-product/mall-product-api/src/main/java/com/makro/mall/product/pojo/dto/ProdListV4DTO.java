package com.makro.mall.product.pojo.dto;

import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProdListV4DTO extends BaseEntity implements Serializable {

    private String id;

    @ApiModelProperty(value = "父ID")
    private String parentId;

    @ApiModelProperty(value = "渠道类型：classic/eco")
    private String channelType;

    @ApiModelProperty(value = "商品编码", required = true)
    private String urlparam;

    @ApiModelProperty(value = "商品名称（本地语言）", required = true)
    private String namethai;

    @ApiModelProperty(value = "商品名称（英文）", required = true)
    private String nameen;

    @ApiModelProperty(value = "规格")
    private String pack;

    @ApiModelProperty(value = "型号")
    private String model;

    @ApiModelProperty(value = "原价", required = true)
    private BigDecimal normalprice;

    @ApiModelProperty(value = "促销价", required = true)
    private BigDecimal promoprice;

    @ApiModelProperty(value = "促销描述")
    private String promotedesc;


    @ApiModelProperty(value = "N件M元，注意没有1的description")
    private Long qty1;

    private BigDecimal promoprice1;

    private String qty1unit;

    private Long qty2;

    private BigDecimal promoprice2;

    private String qty2unit;

    private Long qty3;

    private BigDecimal promoprice3;

    private String qty3unit;

    private Long qty4;

    private BigDecimal promoprice4;

    private String qty4unit;

    private String promoprice2description;

    private String promoprice3description;

    private String promoprice4description;

    @ApiModelProperty(value = "页码", required = true)
    private Integer page;

    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    @ApiModelProperty(value = "促销类型：1正常，2关联商品", required = true)
    private Integer promotype;

    private String icon1;

    private String icon2;

    private String icon3;

    @ApiModelProperty(value = "图表备注")
    private String iconRemark;

    @ApiModelProperty(value = "关联商品编码，可多个，以“/”分割")
    private String linkitemno;

    private String remark1;

    private String remark2;

    private String remark3;

    @ApiModelProperty(value = "类别编码")
    private Long categoryid;

    private String creator;

    private String mmCode;

    private String infoid;

    @ApiModelProperty(value = "0:无效，1:有效")
    private Integer isvalid;

    @ApiModelProperty(value = "商品图片ID")
    private String picid;

    @ApiModelProperty(value = "关联商品的父商品")
    private String parentCode;

    private PicProperties pic;

    private Long productId;

}
