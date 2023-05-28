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
 * 商品历史价格表
 *
 * @TableName PROD_PRICE
 */
@TableName(value = "PROD_PRICE")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProdPrice extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId
    private String id;

    /**
     * MM活动编码
     */
    private String mmCode;

    /**
     * 商品明细来源
     */
    private String dataid;

    /**
     * 原价
     */
    private BigDecimal normalprice;

    /**
     * 促销价
     */
    private BigDecimal promoprice;

    /**
     *
     */
    private String storecode;

    private String itemcode;

    private Integer isvalid;

    private String creator;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
