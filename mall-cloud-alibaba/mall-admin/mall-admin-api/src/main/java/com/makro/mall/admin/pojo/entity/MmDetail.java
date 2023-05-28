package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * MM明细表
 *
 * @TableName MM_DETAIL
 */
@TableName(value = "MM_DETAIL")
@Data
public class MmDetail implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * MM CODE
     */
    private String mmCode;

    /**
     *
     */
    private String itemCode;

    /**
     *
     */
    private Long pageSort;

    /**
     *
     */
    private Long sort;

    /**
     *
     */
    private Long isValid;

    /**
     *
     */
    private Long status;

    /**
     * prod_data_id
     */
    private String prodDataId;

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
        MmDetail other = (MmDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getMmCode() == null ? other.getMmCode() == null : this.getMmCode().equals(other.getMmCode()))
                && (this.getItemCode() == null ? other.getItemCode() == null : this.getItemCode().equals(other.getItemCode()))
                && (this.getPageSort() == null ? other.getPageSort() == null : this.getPageSort().equals(other.getPageSort()))
                && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
                && (this.getIsValid() == null ? other.getIsValid() == null : this.getIsValid().equals(other.getIsValid()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getMmCode() == null) ? 0 : getMmCode().hashCode());
        result = prime * result + ((getItemCode() == null) ? 0 : getItemCode().hashCode());
        result = prime * result + ((getPageSort() == null) ? 0 : getPageSort().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getIsValid() == null) ? 0 : getIsValid().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", mmCode=").append(mmCode);
        sb.append(", itemCode=").append(itemCode);
        sb.append(", pageSort=").append(pageSort);
        sb.append(", sort=").append(sort);
        sb.append(", isValid=").append(isValid);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}