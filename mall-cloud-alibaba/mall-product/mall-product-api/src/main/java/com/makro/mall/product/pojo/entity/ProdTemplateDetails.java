package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 导入模板的数据
 *
 * @TableName PROD_TEMPLATE_DETAILS
 */
@TableName(value = "PROD_TEMPLATE_DETAILS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProdTemplateDetails extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 采购员
     */
    private String buyerid;

    /**
     * JOINT
     */
    private String joint;

    /**
     * 其他海报
     */
    private String othermail;

    /**
     * 多件购买阶梯
     */
    private String multibuy;

    /**
     * 商品编码
     */
    private String itemcode;

    /**
     * 商品名
     */
    private String namethai;

    /**
     * 英文名称
     */
    private String nameeng;

    /**
     * 图片关联
     */
    private String photoref;

    /**
     * 原价
     */
    private BigDecimal normalprice;

    /**
     * 促销价
     */
    private BigDecimal promoprice;

    /**
     * 其他促销价
     */
    private BigDecimal promopriceeco;

    /**
     * 位置异常
     */
    private String locationexception;

    /**
     * 价格异常
     */
    private String priceexception;

    /**
     * 原备注
     */
    private String remark;

    /**
     * 利润
     */
    private String profitclassic;

    /**
     * 其他利润
     */
    private String profiteco;

    /**
     * 箱规
     */
    private Long unitpack;

    /**
     * 单品价格
     */
    private BigDecimal priceunit;

    /**
     * 销售量
     */
    private BigDecimal salesqty;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 总价
     */
    private BigDecimal price;

    /**
     * 让利
     */
    private BigDecimal profitlost;

    /**
     * 折扣比例
     */
    private BigDecimal margin;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 导入文件ID
     */
    private String infoid;

    /**
     * 类别编码
     */
    private Integer categoryid;

    /**
     * 主表UUID
     */
    private String dataid;

    private Integer isvalid;

    private String mmCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
