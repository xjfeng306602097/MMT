package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户信息变更日志表
 *
 * @TableName MM_CUSTOMER_SEGMENT_LOG
 */
@TableName(value = "MM_CUSTOMER_SEGMENT_LOG")
@Data
public class MmCustomerSegmentLog implements Serializable {
    /**
     * 客户表ID
     */
    private Long customerId;

    /**
     * 当前读
     */
    private String currentLog;

    /**
     * 修改读
     */
    private String changeLog;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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
        MmCustomerSegmentLog other = (MmCustomerSegmentLog) that;
        return (this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId()))
                && (this.getCurrentLog() == null ? other.getCurrentLog() == null : this.getCurrentLog().equals(other.getCurrentLog()))
                && (this.getChangeLog() == null ? other.getChangeLog() == null : this.getChangeLog().equals(other.getChangeLog()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
        result = prime * result + ((getCurrentLog() == null) ? 0 : getCurrentLog().hashCode());
        result = prime * result + ((getChangeLog() == null) ? 0 : getChangeLog().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", customerId=").append(customerId);
        sb.append(", currentLog=").append(currentLog);
        sb.append(", changeLog=").append(changeLog);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}