package com.makro.mall.file.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 字体表
 *
 * @TableName MM_FONT
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_FONT")
@Data
public class MmFont extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 字体名
     */
    private String name;
    /**
     * 按键组合
     */
    private String keyCombination;
    /**
     * 字体存放路径
     */
    private String path;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否被删除,0-否,1-是
     */
    @ApiModelProperty("逻辑删除标识 0-未删除 1-已删除")
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;
    private String fontWeight;
    private String boldPath;
    private String italicsPath;
    private String boldItalicsPath;

    private Double pathMb;
    private Double boldPathMb;
    private Double italicsPathMb;
    private Double boldItalicsPathMb;
}