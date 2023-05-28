package com.makro.mall.product.pojo.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 添加或导入模板里商品设计相关数据，MM商品明细的来源
 *
 * @TableName PROD_LIST
 */
@TableName(value = "PROD_LIST")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProdList extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @ExcelIgnore
    private String id;

    /**
     * 渠道类型
     */
    @ApiModelProperty(value = "渠道类型：classic/eco")
    @ExcelProperty("Type")
    private String channelType;

    /**
     * 商城链接编码
     */
    @ApiModelProperty(value = "商品编码", required = true)
    @ExcelProperty({"one for makroclick\n", "Article/\n" +
            "URLparam"})
    private String urlparam;


    /**
     * 商品泰文名
     */
    @ExcelProperty("Item Description TH")
    @ApiModelProperty(value = "商品名称（本地语言）", required = true)
    private String namethai;

    /**
     * 商品英文名
     */
    @ExcelProperty("Item Description EN")
    @ApiModelProperty(value = "商品名称（本地语言）", required = true)
    private String nameen;


    /**
     * 规格
     */
    @ExcelProperty({"เว้นวรรค 1 ครั้ง ในทุกการพิมพ์คำ\n", "Package ขนาด"})
    private String pack;

    /**
     * 型号
     */
    @ExcelProperty("Model")
    private String model;

    /**
     * 原价
     */
    @ExcelProperty({"คีย์เฉพาะตัวเลขเท่านั้น", "Normal Price"})
    @ApiModelProperty(value = "原价", required = true)
    private BigDecimal normalprice;

    /**
     * 促销价
     */
    @ExcelProperty({"คีย์เฉพาะตัวเลขเท่านั้น", "Promo Price"})
    @ApiModelProperty(value = "促销价", required = true)
    private BigDecimal promoprice;

    /**
     * 促销描述
     */
    @ExcelProperty("Promoprice  des")
    @ApiModelProperty(value = "促销描述")
    private String promotedesc;


    /**
     * 页码
     */
    @ExcelProperty("Page")
    @ApiModelProperty(value = "页码", required = true)
    private Integer page;

    /**
     * 排序
     */
    @ExcelProperty("Sort")
    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;


    /**
     * 图标1
     */
    @ExcelProperty("Icon(1)")
    private String icon1;

    /**
     * 图标2
     */
    @ExcelProperty("Icon(2)")
    private String icon2;

    /**
     * 图标3
     */
    @ExcelProperty("Icon(3)")
    private String icon3;
    /**
     * 图表备注
     */
    @ExcelProperty("Icon (Remark)")
    private String iconRemark;
    /**
     * 促销类型：1正常，2关联商品
     */
    @ApiModelProperty(value = "促销类型：1正常，2关联商品", required = true)
    @ExcelProperty("PromoType")
    private Integer promotype;

    /**
     * 关联商品编码，可多个，以“/”分割
     */
    @ExcelProperty("LinkItemNo")
    @ApiModelProperty(value = "关联商品编码，可多个，以“/”分割", required = false)
    private String linkitemno;


    /**
     * 备注1
     */
    @ExcelProperty("Remark1")
    private String remark1;

    /**
     * 备注2
     */
    @ExcelProperty("Remark2")
    private String remark2;

    /**
     * 备注3
     */
    @ExcelProperty("Remark3")
    private String remark3;

    @ExcelProperty("Product ID")
    private String productId;

    @ApiModelProperty(value = "N件M元，注意没有1的description")
    @ExcelIgnore
    private Long qty1;
    @ExcelIgnore
    private BigDecimal promoprice1;
    @ExcelIgnore
    private String qty1unit;
    @ExcelIgnore
    private Long qty2;
    @ExcelIgnore
    private BigDecimal promoprice2;
    @ExcelIgnore
    private String qty2unit;
    @ExcelIgnore
    private Long qty3;
    @ExcelIgnore
    private BigDecimal promoprice3;
    @ExcelIgnore
    private String qty3unit;
    @ExcelIgnore
    private Long qty4;
    @ExcelIgnore
    private BigDecimal promoprice4;
    @ExcelIgnore
    private String qty4unit;
    @ExcelIgnore
    private String promoprice2description;
    @ExcelIgnore
    private String promoprice3description;
    @ExcelIgnore
    private String promoprice4description;

    /**
     * 类别编码
     */
    @ExcelIgnore
    private Long categoryid;

    /**
     * 创建者
     */
    @ExcelIgnore
    private String creator;

    /**
     * MM UUID
     */
    @ExcelIgnore
    private String mmCode;

    /**
     *
     */
    @ExcelIgnore
    private String infoid;


    /**
     * 0:无效，1:有效
     */
    @ExcelIgnore
    private Integer isvalid;

    /**
     * 商品图片ID
     */
    @ExcelIgnore
    private String picid;

    /**
     * 关联商品的父商品
     */
    @ExcelIgnore
    @ApiModelProperty(value = "关联商品的父商品")
    private String parentCode;


    @ApiModelProperty(value = "父ID")
    @ExcelIgnore
    private String parentId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
