package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @TableName SYS_DICT_ITEM
 */
@TableName(value = "SYS_DICT_ITEM")
@Data
public class SysDictItem extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 字典项名称
     */
    @ApiModelProperty(value = "字典项名称", required = true)
    private String name;

    /**
     * 字典项值
     */
    @ApiModelProperty(value = "字典项值", required = true)
    private String value;

    /**
     * 字典编码
     */
    @ApiModelProperty(value = "字典编码", required = true)
    @NotEmpty
    private String dictCode;

    /**
     * 字典id
     */
    @ApiModelProperty(value = "字典id", required = true)
    @NotNull
    private Integer parentId;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序，默认传1", required = true)
    private Long sort;

    /**
     * 状态（0-正常 ,1-停用）
     */
    @ApiModelProperty(value = "状态（0-正常 ,1-停用）,默认传1", required = true)
    private Long status;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除（0-正常 ,1-被删除）", required = true)
    @TableLogic(delval = "1", value = "0")
    private Long deleted;

    /**
     * 是否默认（0否 1是）
     */
    @ApiModelProperty(value = "是否默认（0否 1是）", required = true)
    private Long defaulted;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysDictItem other = (SysDictItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()))
                && (this.getDictCode() == null ? other.getDictCode() == null : this.getDictCode().equals(other.getDictCode()))
                && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getDefaulted() == null ? other.getDefaulted() == null : this.getDefaulted().equals(other.getDefaulted()))
                && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
                && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
                && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        result = prime * result + ((getDictCode() == null) ? 0 : getDictCode().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDefaulted() == null) ? 0 : getDefaulted().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", value=").append(value);
        sb.append(", dictCode=").append(dictCode);
        sb.append(", sort=").append(sort);
        sb.append(", status=").append(status);
        sb.append(", defaulted=").append(defaulted);
        sb.append(", remark=").append(remark);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}