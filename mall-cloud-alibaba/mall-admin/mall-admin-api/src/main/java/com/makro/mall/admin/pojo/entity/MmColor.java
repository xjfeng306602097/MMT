package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @TableName MM_COLOR
 */
@TableName(value = "MM_COLOR")
@Data
@NoArgsConstructor
public class MmColor extends BaseEntity implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank
    private String rgb;

    @NotBlank
    private String cmyk;

    @NotBlank
    private String hex;

    private Integer sort;

    private Integer status;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

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
        MmColor other = (MmColor) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getRgb() == null ? other.getRgb() == null : this.getRgb().equals(other.getRgb()))
                && (this.getCmyk() == null ? other.getCmyk() == null : this.getCmyk().equals(other.getCmyk()))
                && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
                && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
                && (this.getLastUpdater() == null ? other.getLastUpdater() == null : this.getLastUpdater().equals(other.getLastUpdater()))
                && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRgb() == null) ? 0 : getRgb().hashCode());
        result = prime * result + ((getCmyk() == null) ? 0 : getCmyk().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getLastUpdater() == null) ? 0 : getLastUpdater().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", rgb=").append(rgb);
        sb.append(", cmyk=").append(cmyk);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", lastUpdater=").append(lastUpdater);
        sb.append(", creator=").append(creator);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}