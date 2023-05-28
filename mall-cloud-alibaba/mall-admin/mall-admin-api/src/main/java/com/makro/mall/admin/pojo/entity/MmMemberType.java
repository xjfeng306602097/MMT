package com.makro.mall.admin.pojo.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author jincheng
 * @TableName MM_MEMBER_TYPE
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_MEMBER_TYPE")
@Data
public class MmMemberType extends BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private String id;

    private String nameTh;

    private String nameEn;

    private Boolean active;

    /**
     * 1-被删除，0-未删除
     */
    @ApiModelProperty(value = "是否删除，1-删除，0-否")
    @TableLogic(value = "0", delval = "1")
    private Long deleted;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}