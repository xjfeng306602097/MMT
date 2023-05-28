package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName visitor_cal_count
 */
@TableName(value = "visitor_cal_count")
@Data
@Builder
public class VisitorCalCount implements Serializable {
    /**
     *
     */
    private String mmCode;

    /**
     *
     */
    private String calHour;

    /**
     *
     */
    private Long num;

    /**
     *
     */
    private Date calDate;

    /**
     *
     */
    private Date createTime;

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
        VisitorCalCount other = (VisitorCalCount) that;
        return (this.getMmCode() == null ? other.getMmCode() == null : this.getMmCode().equals(other.getMmCode()))
                && (this.getCalHour() == null ? other.getCalHour() == null : this.getCalHour().equals(other.getCalHour()))
                && (this.getNum() == null ? other.getNum() == null : this.getNum().equals(other.getNum()))
                && (this.getCalDate() == null ? other.getCalDate() == null : this.getCalDate().equals(other.getCalDate()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMmCode() == null) ? 0 : getMmCode().hashCode());
        result = prime * result + ((getCalHour() == null) ? 0 : getCalHour().hashCode());
        result = prime * result + ((getNum() == null) ? 0 : getNum().hashCode());
        result = prime * result + ((getCalDate() == null) ? 0 : getCalDate().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", mmCode=").append(mmCode);
        sb.append(", calHour=").append(calHour);
        sb.append(", num=").append(num);
        sb.append(", calDate=").append(calDate);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}