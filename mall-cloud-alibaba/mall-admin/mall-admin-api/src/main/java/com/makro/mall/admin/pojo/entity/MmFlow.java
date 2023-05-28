package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * mm流转流程
 *
 * @TableName MM_FLOW
 */
@TableName(value = "MM_FLOW")
@Data
public class MmFlow extends BaseEntity implements Serializable {

    public static final Long STATUS_COMPLETED = 99L;
    public static final Long STATUS_AUTO_COMPLETED = 999L;
    public static final Long STATUS_NEW = 0L;
    public static final Long STATUS_IN_PROGRESS_APPROVE = 1L;
    public static final Long STATUS_IN_PROGRESS_REJECT = 2L;
    public static final Long STATUS_IN_PROGRESS_CLOSED = 100L;

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    @ApiModelProperty(value = "MM的code或模板code,流程1用模板code,流程2,3用mm code", required = true)
    private String code;

    /**
     *
     */
    @ApiModelProperty(value = "流程名称")
    private String name;

    /**
     *
     */
    @ApiModelProperty(value = "当前角色")
    private String currentRole;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;

    /**
     * 0-初始化,1-上一个节点通过,2-上一个节点拒绝，99-完结
     */
    @ApiModelProperty(value = "状态值,0-初始化,1-上一个节点通过,2-上一个节点拒绝，99-完结,100-回退到发起人，直接结束")
    private Long status;

    /**
     *
     */
    @TableLogic(delval = "1", value = "0")
    private Long deleted;

    /**
     *
     */
    @ApiModelProperty(value = "流程类型,详细看-获取流程类型接口")
    private String type;

    /**
     *
     */
    @ApiModelProperty(value = "最新一条的审核记录")
    private Long lastDetailId;

    /**
     * 配置json
     */
    @ApiModelProperty(value = "流程流转配置json")
    private String configJson;

    /**
     * 当前步骤
     */
    @ApiModelProperty(value = "流程节点")
    private Integer step;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否过滤为只为自己审核的数据")
    private boolean justMe = false;

    @ApiModelProperty(value = "预览路径")
    private String previewUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ApiModelProperty(value = "描述")
    private String description;

    @TableField(exist = false)
    @ApiModelProperty(value = "起始查询时间")
    private Date gmtCreateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "终止查询时间")
    private Date gmtCreateEnd;


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
        MmFlow other = (MmFlow) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getCurrentRole() == null ? other.getCurrentRole() == null : this.getCurrentRole().equals(other.getCurrentRole()))
                && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()))
                && (this.getLastUpdater() == null ? other.getLastUpdater() == null : this.getLastUpdater().equals(other.getLastUpdater()))
                && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
                && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getLastDetailId() == null ? other.getLastDetailId() == null : this.getLastDetailId().equals(other.getLastDetailId()))
                && (this.getConfigJson() == null ? other.getConfigJson() == null : this.getConfigJson().equals(other.getConfigJson()))
                && (this.getStep() == null ? other.getStep() == null : this.getStep().equals(other.getStep()))
                && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCurrentRole() == null) ? 0 : getCurrentRole().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        result = prime * result + ((getLastUpdater() == null) ? 0 : getLastUpdater().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getLastDetailId() == null) ? 0 : getLastDetailId().hashCode());
        result = prime * result + ((getConfigJson() == null) ? 0 : getConfigJson().hashCode());
        result = prime * result + ((getStep() == null) ? 0 : getStep().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", name=").append(name);
        sb.append(", currentRole=").append(currentRole);
        sb.append(", creator=").append(creator);
        sb.append(", lastUpdater=").append(lastUpdater);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", type=").append(type);
        sb.append(", lastDetailId=").append(lastDetailId);
        sb.append(", configJson=").append(configJson);
        sb.append(", step=").append(step);
        sb.append(", description=").append(description);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public boolean isDone() {
        return this.status.equals(STATUS_COMPLETED) || this.status.equals(STATUS_AUTO_COMPLETED);
    }

}