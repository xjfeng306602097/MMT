package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * MM STORE
 *
 * @TableName MM_STORE
 */
@TableName(value = "MM_STORE")
@Data
public class MmStore implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 1-上线2-下线
     */
    private Long status;

    /**
     *
     */
    @TableLogic(delval = "1", value = "0")
    private Long deleted;

    /**
     * 排序
     */
    private Long sort;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}