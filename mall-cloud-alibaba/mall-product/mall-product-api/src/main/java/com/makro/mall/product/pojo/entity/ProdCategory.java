package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品类别表
 *
 * @TableName PROD_CATEGORY
 */
@TableName(value = "PROD_CATEGORY")
@Data
public class ProdCategory extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId
    private String id;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 类别编码
     */
    @ApiModelProperty(value = "类别编码，来自于主数据商品系统", required = true)
    private String code;

    /**
     * 层级
     */
    @ApiModelProperty(value = "类别层级，固定为DIVISION, GROUP, DEPT, CLASS, SUBCLASS", required = true)
    private String catlevel;

    /**
     * 父类编码
     */
    private String parentCode;

    /**
     * 父类层级
     */
    private String parentLevel;

    /**
     * 排序
     */
    private String sort;

    /**
     * 状态
     */
    private Long status;

    /**
     * 编码树形路径
     */
    @ApiModelProperty(value = "类别编码，来自于主数据商品系统", required = true)
    private String treePath;

    /**
     * 是否有效
     */
    private Long isvalid;

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
        ProdCategory other = (ProdCategory) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
                && (this.getCatlevel() == null ? other.getCatlevel() == null : this.getCatlevel().equals(other.getCatlevel()))
                && (this.getParentCode() == null ? other.getParentCode() == null : this.getParentCode().equals(other.getParentCode()))
                && (this.getParentLevel() == null ? other.getParentLevel() == null : this.getParentLevel().equals(other.getParentLevel()))
                && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getTreePath() == null ? other.getTreePath() == null : this.getTreePath().equals(other.getTreePath()))
                && (this.getIsvalid() == null ? other.getIsvalid() == null : this.getIsvalid().equals(other.getIsvalid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getCatlevel() == null) ? 0 : getCatlevel().hashCode());
        result = prime * result + ((getParentCode() == null) ? 0 : getParentCode().hashCode());
        result = prime * result + ((getParentLevel() == null) ? 0 : getParentLevel().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getTreePath() == null) ? 0 : getTreePath().hashCode());
        result = prime * result + ((getIsvalid() == null) ? 0 : getIsvalid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", catname=").append(name);
        sb.append(", code=").append(code);
        sb.append(", catlevel=").append(catlevel);
        sb.append(", parentCode=").append(parentCode);
        sb.append(", parentLevel=").append(parentLevel);
        sb.append(", sort=").append(sort);
        sb.append(", status=").append(status);
        sb.append(", treePath=").append(treePath);
        sb.append(", isvalid=").append(isvalid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}