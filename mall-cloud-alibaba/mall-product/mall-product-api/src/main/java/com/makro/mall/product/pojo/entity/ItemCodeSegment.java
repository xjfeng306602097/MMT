package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ITEM CODE关联SEGMENT表
 *
 * @author jincheng
 * @TableName ITEM_CODE_SEGMENT
 */
@TableName(value = "ITEM_CODE_SEGMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCodeSegment implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String itemCode;
    /**
     *
     */
    private Long segmentId;
}
