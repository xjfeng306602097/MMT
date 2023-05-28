package com.makro.mall.file.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @author jincheng
 * @TableName MM_ELEMENT_TREE
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_ELEMENT_TREE")
@Data
public class MmElementTree extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Long id;
    /**
     *
     */
    @ApiModelProperty("文件名")
    @Length(max = 80)
    private String name;
    /**
     * 0为根节点
     */
    @ApiModelProperty("父id,0为根节点")
    @Min(0)
    private Long parentId;
    /**
     * 0为文件夹
     */
    @ApiModelProperty("元素id,0为文件夹")
    @Min(0)
    private Long elementId;

    private String type;

    private String remark;
    /**
     *
     */
    private String userId;
    /**
     *
     */
    @TableLogic(delval = "1", value = "0")
    private Long deleted;
}