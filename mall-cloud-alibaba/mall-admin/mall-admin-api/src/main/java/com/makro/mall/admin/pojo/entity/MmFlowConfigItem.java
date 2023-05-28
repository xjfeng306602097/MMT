package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程配置明细
 *
 * @TableName MM_FLOW_CONFIG_ITEM
 */
@TableName(value = "MM_FLOW_CONFIG_ITEM")
@Data
public class MmFlowConfigItem extends BaseEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private String roleCode;

    /**
     *
     */
    private Long roleId;

    /**
     *
     */
    private Long relateConfig;

    /**
     *
     */
    private String remark;

    /**
     *
     */
    private String creator;

    /**
     *
     */
    private String lastUpdater;

    /**
     *
     */
    private Integer step;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String roleName;

    /**
     *
     */
    @TableLogic(delval = "1", value = "0")
    private Long deleted;

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
        MmFlowConfigItem other = (MmFlowConfigItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getRoleCode() == null ? other.getRoleCode() == null : this.getRoleCode().equals(other.getRoleCode()))
                && (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
                && (this.getRelateConfig() == null ? other.getRelateConfig() == null : this.getRelateConfig().equals(other.getRelateConfig()))
                && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
                && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()))
                && (this.getLastUpdater() == null ? other.getLastUpdater() == null : this.getLastUpdater().equals(other.getLastUpdater()))
                && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
                && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
                && (this.getStep() == null ? other.getStep() == null : this.getStep().equals(other.getStep()))
                && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
                && (this.getRoleName() == null ? other.getRoleName() == null : this.getRoleName().equals(other.getRoleName()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoleCode() == null) ? 0 : getRoleCode().hashCode());
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getRelateConfig() == null) ? 0 : getRelateConfig().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        result = prime * result + ((getLastUpdater() == null) ? 0 : getLastUpdater().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getStep() == null) ? 0 : getStep().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getRoleName() == null) ? 0 : getRoleName().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roleCode=").append(roleCode);
        sb.append(", roleId=").append(roleId);
        sb.append(", relateConfig=").append(relateConfig);
        sb.append(", remark=").append(remark);
        sb.append(", creator=").append(creator);
        sb.append(", lastUpdater=").append(lastUpdater);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", step=").append(step);
        sb.append(", title=").append(title);
        sb.append(", roleName=").append(roleName);
        sb.append(", deleted=").append(deleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}