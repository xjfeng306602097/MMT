package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * MM流程明细
 *
 * @TableName MM_FLOW_DETAILS
 */
@TableName(value = "MM_FLOW_DETAILS")
@Data
public class MmFlowDetails extends BaseEntity implements Serializable {

    public static final String WORKFLOW_CLOSED = "workflow closed";

    public static final String WORKFLOW_FINISHED = "workflow finished";

    public static final String FLOW_OPTION_APPROVE = "APPROVE";

    public static final String FLOW_OPTION_REJECT = "REJECT";

    public static final String FLOW_OPTION_NONE = "NONE";

    public static final String FLOW_OPTION_CREATE = "CREATE";

    public static final Set<String> OPTION_SET = new HashSet<String>() {
        {
            add(FLOW_OPTION_APPROVE);
            add(FLOW_OPTION_REJECT);
            add(FLOW_OPTION_NONE);
            add(FLOW_OPTION_CREATE);
        }
    };


    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    @ApiModelProperty(value = "审核流程的id", required = true)
    private String code;

    /**
     *
     */
    @ApiModelProperty(value = "授权用户，不需要传")
    private String authUser;

    /**
     * 操作，具体做了什么内容
     */
    @ApiModelProperty(value = "操作,对应为APPROVE-通过,REJECT-拒绝", required = true)
    private String flowOption;

    /**
     * 指定的授权角色
     */
    @ApiModelProperty(value = "下一个审核的角色,默认不填")
    private String nextAuthRole;

    /**
     *
     */
    @TableLogic(delval = "1", value = "0")
    private Long deleted;

    /**
     *
     */
    @ApiModelProperty(value = "上一个审核的角色,默认不填")
    private String lastAuthRole;

    /**
     * 流程第几步
     */
    @ApiModelProperty(value = "流程节点")
    private Integer step;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径，逗号分隔")
    private String filePath;


    /**
     *
     */
    @ApiModelProperty(value = "步骤名称", required = false)
    private String stepName;

    /**
     *
     */
    @ApiModelProperty(value = "备注", required = false)
    private byte[] remark;


    @TableField(exist = false)
    @ApiModelProperty(value = "前端传入的备注", required = true)
    private String htmlRemark;

    public void convertRemark() {
        if (this.remark != null && this.remark.length > 0) {
            this.htmlRemark = new String(this.remark, StandardCharsets.UTF_8);
        }
    }

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
        MmFlowDetails other = (MmFlowDetails) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
                && (this.getAuthUser() == null ? other.getAuthUser() == null : this.getAuthUser().equals(other.getAuthUser()))
                && (this.getFlowOption() == null ? other.getFlowOption() == null : this.getFlowOption().equals(other.getFlowOption()))
                && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
                && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
                && (this.getNextAuthRole() == null ? other.getNextAuthRole() == null : this.getNextAuthRole().equals(other.getNextAuthRole()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
                && (this.getLastAuthRole() == null ? other.getLastAuthRole() == null : this.getLastAuthRole().equals(other.getLastAuthRole()))
                && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
                && (this.getStep() == null ? other.getStep() == null : this.getStep().equals(other.getStep()))
                && (this.getStepName() == null ? other.getStepName() == null : this.getStepName().equals(other.getStepName()))
                && (Arrays.equals(this.getRemark(), other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getAuthUser() == null) ? 0 : getAuthUser().hashCode());
        result = prime * result + ((getFlowOption() == null) ? 0 : getFlowOption().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getNextAuthRole() == null) ? 0 : getNextAuthRole().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getLastAuthRole() == null) ? 0 : getLastAuthRole().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getStep() == null) ? 0 : getStep().hashCode());
        result = prime * result + ((getStepName() == null) ? 0 : getStepName().hashCode());
        result = prime * result + (Arrays.hashCode(getRemark()));
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
        sb.append(", authUser=").append(authUser);
        sb.append(", flowOption=").append(flowOption);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", nextAuthRole=").append(nextAuthRole);
        sb.append(", deleted=").append(deleted);
        sb.append(", lastAuthRole=").append(lastAuthRole);
        sb.append(", filePath=").append(filePath);
        sb.append(", step=").append(step);
        sb.append(", stepName=").append(stepName);
        sb.append(", remark=").append(Arrays.toString(remark));
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

}