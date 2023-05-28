package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 模板主表信息
 *
 * @TableName PROD_TEMPLATE_INFO
 */
@TableName(value = "PROD_TEMPLATE_INFO")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ProdTemplateInfo extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * MM UUID
     */
    private String mmCode;

    /**
     * 导入文件名
     */
    private String filename;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否有效
     */
    private Integer isvalid;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 导入SHEET名
     */
    private String sheetname;

    /**
     * 表头信息组合
     */
    private String promoinfo;

    /**
     * 导入数据量
     */
    private Long datanum;

    /**
     * 结果（成功/警告）
     */
    private String importresult;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
