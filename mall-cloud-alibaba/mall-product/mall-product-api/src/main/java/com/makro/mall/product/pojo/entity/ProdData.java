package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 添加或导入模板里的原始商品数据，MM商品明细的来源
 *
 * @TableName PROD_DATA
 */
@TableName(value = "PROD_DATA")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProdData extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 商品编码
     */
    private String itemcode;

    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名称（本地语言）", required = true)
    private String namethai;

    /**
     * 英文名称
     */
    @ApiModelProperty(value = "商品名称（英文）", required = true)
    private String nameeng;

    /**
     * 原价
     */
    @ApiModelProperty(value = "原价", required = true)
    private BigDecimal normalprice;

    /**
     * 促销价
     */
    @ApiModelProperty(value = "促销价", required = true)
    private BigDecimal promoprice;

    /**
     * 原备注
     */
    @ApiModelProperty(value = "TextThai备注", required = false)
    private String remark;

    /**
     * 商城链接编码
     */
    @ApiModelProperty(value = "商城链接编码", required = true)
    private String urlparma;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", required = true)
    private Integer page;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    /**
     * 促销类型：1正常，2赠品，3组合
     */
    @ApiModelProperty(value = "促销类型：1正常，2赠品，3组合", required = true)
    private Integer promotype;

    /**
     * 销售单位
     */
    @ApiModelProperty(value = "销售单位", required = true)
    private String saleunit;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格", required = false)
    private String pack;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", required = false)
    private String description;

    /**
     * 品牌
     */
    @ApiModelProperty(value = "品牌(大小写均会转化为大写)", required = false)
    private String brand;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号", required = false)
    private String model;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private Long qty2;

    @ApiModelProperty(value = "N件M元单位", required = false)
    private String qty2unit;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private BigDecimal promoprice2;

    @ApiModelProperty(value = "N件M元描述", required = false)
    private String promoprice2description;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private Long qty3;

    @ApiModelProperty(value = "N件M元单位", required = false)
    private String qty3unit;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private BigDecimal promoprice3;

    @ApiModelProperty(value = "N件M元描述", required = false)
    private String promoprice3description;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private Long qty4;

    @ApiModelProperty(value = "N件M元单位", required = false)
    private String qty4unit;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private BigDecimal promoprice4;

    @ApiModelProperty(value = "N件M元描述", required = false)
    private String promoprice4description;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private Long qty5;

    @ApiModelProperty(value = "N件M元单位", required = false)
    private String qty5unit;

    /**
     * N件M元
     */
    @ApiModelProperty(value = "N件M元", required = false)
    private BigDecimal promoprice5;

    @ApiModelProperty(value = "N件M元描述", required = false)
    private String promoprice5description;

    @ApiModelProperty(value = "均价1", required = false)
    private BigDecimal avgprice1;

    @ApiModelProperty(value = "均价2", required = false)
    private BigDecimal avgprice2;

    @ApiModelProperty(value = "均价3", required = false)
    private BigDecimal avgprice3;

    @ApiModelProperty(value = "口味", required = false)
    private String taste;

    @ApiModelProperty(value = "多条码", required = false)
    private String mutiitemcode;

    @ApiModelProperty(value = "产品结构比例", required = false)
    private String structuralproportion;

    /**
     * 促销描述
     */
    @ApiModelProperty(value = "促销描述", required = false)
    private String promotedesc;

    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额", required = false)
    private BigDecimal discountamount;

    /**
     * 折扣百分比值
     */
    @ApiModelProperty(value = "折扣百分比值", required = false)
    private BigDecimal discountpercent;

    /**
     * 赠品编码
     */
    @ApiModelProperty(value = "赠品编码", required = false)
    private String giftitemno;

    /**
     * 备注1
     */
    @ApiModelProperty(value = "备注1", required = false)
    private String remark1;

    /**
     * 备注2
     */
    @ApiModelProperty(value = "备注2", required = false)
    private String remark2;

    /**
     * 备注3
     */
    @ApiModelProperty(value = "备注3", required = false)
    private String remark3;

    /**
     * 图标1
     */
    @ApiModelProperty(value = "图标1", required = false)
    private String icon1;

    /**
     * 图标2
     */
    @ApiModelProperty(value = "图标2", required = false)
    private String icon2;

    /**
     * 图标3
     */
    @ApiModelProperty(value = "图标3", required = false)
    private String icon3;

    /**
     * 规格，20升填20
     */
    @ApiModelProperty(value = "规格，20升填20(已废弃，可作为扩展字段)", required = false)
    private String unittitle1;

    /**
     * 规格，20升填升
     */
    @ApiModelProperty(value = "规格，20升填升(已废弃，可作为扩展字段)", required = false)
    private String unitvalue1;

    /**
     * 规格，20升填20
     */
    @ApiModelProperty(value = "规格，20升填20(已废弃，可作为扩展字段)", required = false)
    private String unittitle2;

    /**
     * 规格，20升填升
     */
    @ApiModelProperty(value = "规格，20升填升(已废弃，可作为扩展字段)", required = false)
    private String unitvalue2;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者", required = false)
    private String creator;

    /**
     * 类别编码
     */
    @ApiModelProperty(value = "类别编码，subClass", required = false)
    private Integer categoryid;

    /**
     * MM UUID
     */
    @ApiModelProperty(value = "MM的Code", required = false)
    private String mmCode;

    /**
     * 导入模板的主表ID
     */
    @ApiModelProperty(value = "导入模板的主表ID，手工则为null", required = false)
    private String infoid;

    @ApiModelProperty(value = "是否有效，1：有效；0：无效", required = true)
    private Integer isvalid;

    @ApiModelProperty(value = "图片ID，匹配Prod_Pic表", required = false)
    private String picid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
