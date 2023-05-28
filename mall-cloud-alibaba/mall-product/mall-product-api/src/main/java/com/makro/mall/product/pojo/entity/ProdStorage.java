package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品库
 *
 * @TableName PROD_STORAGE
 */
@TableName(value = "PROD_STORAGE")
@Data
public class ProdStorage extends BaseEntity implements Serializable {
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
    private String namethai;

    /**
     * 英文名称
     */
    private String nameen;


    /**
     * 销售单位
     */
    private String qty1unit;

    /**
     * 规格
     */
    private String pack;


    /**
     * 型号
     */
    private String model;

    private BigDecimal normalprice;

    private BigDecimal promoprice;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 类别编码
     */
    private Integer categoryid;

    /**
     * 最后更新关联的dataUUID
     */
    private String lastmodifydataid;

    private Integer isvalid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
