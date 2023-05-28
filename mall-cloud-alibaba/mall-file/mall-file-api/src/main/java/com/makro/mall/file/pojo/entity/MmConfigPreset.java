package com.makro.mall.file.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 模板大小配置
 *
 * @TableName MM_CONFIG_SIZE
 */
@TableName(value = "MM_CONFIG_SIZE")
@Data
public class MmConfigPreset extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     *
     */
    private Long type;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private BigDecimal width;
    /**
     *
     */
    private BigDecimal height;
    /**
     *
     */
    private BigDecimal sizeRate;
    /**
     *
     */
    private Long dpi;
    /**
     * 尺寸单位ID
     */
    private Long unit;
    /**
     *
     */
    private BigDecimal marginTop;
    /**
     *
     */
    private BigDecimal marginIn;
    /**
     *
     */
    private BigDecimal marginOut;
    /**
     *
     */
    private BigDecimal marginBottom;
    /**
     *
     */
    private BigDecimal bleedLineTop;
    /**
     *
     */
    private BigDecimal bleedLineIn;
    /**
     *
     */
    private BigDecimal bleedLineOut;
    /**
     *
     */
    private BigDecimal bleedLineBottom;
    /**
     *
     */
    private String remark;
    /**
     *
     */
    private Long sort;
    /**
     *
     */
    private Long status;
    /**
     * 1-被删除,0-未删除
     */
    @TableLogic(value = "0", delval = "1")
    private Long deleted;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;

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
        MmConfigPreset other = (MmConfigPreset) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getWidth() == null ? other.getWidth() == null : this.getWidth().equals(other.getWidth()))
                && (this.getHeight() == null ? other.getHeight() == null : this.getHeight().equals(other.getHeight()))
                && (this.getDpi() == null ? other.getDpi() == null : this.getDpi().equals(other.getDpi()))
                && (this.getUnit() == null ? other.getUnit() == null : this.getUnit().equals(other.getUnit()))
                && (this.getMarginTop() == null ? other.getMarginTop() == null : this.getMarginTop().equals(other.getMarginTop()))
                && (this.getMarginIn() == null ? other.getMarginIn() == null : this.getMarginIn().equals(other.getMarginIn()))
                && (this.getMarginOut() == null ? other.getMarginOut() == null : this.getMarginOut().equals(other.getMarginOut()))
                && (this.getMarginBottom() == null ? other.getMarginBottom() == null : this.getMarginBottom().equals(other.getMarginBottom()))
                && (this.getBleedLineTop() == null ? other.getBleedLineTop() == null : this.getBleedLineTop().equals(other.getBleedLineTop()))
                && (this.getBleedLineIn() == null ? other.getBleedLineIn() == null : this.getBleedLineIn().equals(other.getBleedLineIn()))
                && (this.getBleedLineOut() == null ? other.getBleedLineOut() == null : this.getBleedLineOut().equals(other.getBleedLineOut()))
                && (this.getBleedLineBottom() == null ? other.getBleedLineBottom() == null : this.getBleedLineBottom().equals(other.getBleedLineBottom()))
                && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
                && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
                && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()))
                && (this.getLastUpdater() == null ? other.getLastUpdater() == null : this.getLastUpdater().equals(other.getLastUpdater()))
                && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
                && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getWidth() == null) ? 0 : getWidth().hashCode());
        result = prime * result + ((getHeight() == null) ? 0 : getHeight().hashCode());
        result = prime * result + ((getDpi() == null) ? 0 : getDpi().hashCode());
        result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
        result = prime * result + ((getMarginTop() == null) ? 0 : getMarginTop().hashCode());
        result = prime * result + ((getMarginIn() == null) ? 0 : getMarginIn().hashCode());
        result = prime * result + ((getMarginOut() == null) ? 0 : getMarginOut().hashCode());
        result = prime * result + ((getMarginBottom() == null) ? 0 : getMarginBottom().hashCode());
        result = prime * result + ((getBleedLineTop() == null) ? 0 : getBleedLineTop().hashCode());
        result = prime * result + ((getBleedLineIn() == null) ? 0 : getBleedLineIn().hashCode());
        result = prime * result + ((getBleedLineOut() == null) ? 0 : getBleedLineOut().hashCode());
        result = prime * result + ((getBleedLineBottom() == null) ? 0 : getBleedLineBottom().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        result = prime * result + ((getLastUpdater() == null) ? 0 : getLastUpdater().hashCode());
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
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", dpi=").append(dpi);
        sb.append(", unit=").append(unit);
        sb.append(", marginTop=").append(marginTop);
        sb.append(", marginIn=").append(marginIn);
        sb.append(", marginOut=").append(marginOut);
        sb.append(", marginBottom=").append(marginBottom);
        sb.append(", bleedLineTop=").append(bleedLineTop);
        sb.append(", bleedLineIn=").append(bleedLineIn);
        sb.append(", bleedLineOut=").append(bleedLineOut);
        sb.append(", bleedLineBottom=").append(bleedLineBottom);
        sb.append(", remark=").append(remark);
        sb.append(", sort=").append(sort);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", creator=").append(creator);
        sb.append(", lastUpdater=").append(lastUpdater);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}