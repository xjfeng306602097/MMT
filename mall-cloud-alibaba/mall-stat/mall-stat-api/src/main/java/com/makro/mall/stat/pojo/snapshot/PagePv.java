package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jincheng
 * @TableName page_pv
 */
@TableName(value = "page_pv")
@Data
public class PagePv implements Serializable {
    /**
     *
     */
    private String mmcode;
    /**
     * 当天计数
     */
    private Long count;

    /**
     * 当天的数量累计
     */
    private Long total;

    /**
     * 当天日期
     */
    private Date date;

    /**
     * 昨天计数
     */
    private Long daycount;

    /**
     * 昨天当天的数量累计
     */
    private Long daytotal;

    /**
     * 昨天日期
     */
    private Date daydate;

    /**
     * 上周同期当天计数
     */
    private Long weekcount;

    /**
     * 上周同期当天的数量累计
     */
    private Long weektotal;

    /**
     * 上周同期当天日期
     */
    private Date weekdate;

    /**
     * 上月同期当天计数
     */
    private Long monthcount;

    /**
     * 上月同期当天的数量累计
     */
    private Long monthtotal;

    /**
     * 上月同期当天日期
     */
    private Date monthdate;

    /**
     * 去年同期当天计数
     */
    private Long yearcount;

    /**
     * 去年同期当天的数量累计
     */
    private Long yeartotal;

    /**
     * 去年同期当天日期
     */
    private Date yeardate;

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
        PagePv other = (PagePv) that;
        return (this.getCount() == null ? other.getCount() == null : this.getCount().equals(other.getCount()))
                && (this.getTotal() == null ? other.getTotal() == null : this.getTotal().equals(other.getTotal()))
                && (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
                && (this.getDaycount() == null ? other.getDaycount() == null : this.getDaycount().equals(other.getDaycount()))
                && (this.getDaytotal() == null ? other.getDaytotal() == null : this.getDaytotal().equals(other.getDaytotal()))
                && (this.getDaydate() == null ? other.getDaydate() == null : this.getDaydate().equals(other.getDaydate()))
                && (this.getWeekcount() == null ? other.getWeekcount() == null : this.getWeekcount().equals(other.getWeekcount()))
                && (this.getWeektotal() == null ? other.getWeektotal() == null : this.getWeektotal().equals(other.getWeektotal()))
                && (this.getWeekdate() == null ? other.getWeekdate() == null : this.getWeekdate().equals(other.getWeekdate()))
                && (this.getMonthcount() == null ? other.getMonthcount() == null : this.getMonthcount().equals(other.getMonthcount()))
                && (this.getMonthtotal() == null ? other.getMonthtotal() == null : this.getMonthtotal().equals(other.getMonthtotal()))
                && (this.getMonthdate() == null ? other.getMonthdate() == null : this.getMonthdate().equals(other.getMonthdate()))
                && (this.getYearcount() == null ? other.getYearcount() == null : this.getYearcount().equals(other.getYearcount()))
                && (this.getYeartotal() == null ? other.getYeartotal() == null : this.getYeartotal().equals(other.getYeartotal()))
                && (this.getYeardate() == null ? other.getYeardate() == null : this.getYeardate().equals(other.getYeardate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCount() == null) ? 0 : getCount().hashCode());
        result = prime * result + ((getTotal() == null) ? 0 : getTotal().hashCode());
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getDaycount() == null) ? 0 : getDaycount().hashCode());
        result = prime * result + ((getDaytotal() == null) ? 0 : getDaytotal().hashCode());
        result = prime * result + ((getDaydate() == null) ? 0 : getDaydate().hashCode());
        result = prime * result + ((getWeekcount() == null) ? 0 : getWeekcount().hashCode());
        result = prime * result + ((getWeektotal() == null) ? 0 : getWeektotal().hashCode());
        result = prime * result + ((getWeekdate() == null) ? 0 : getWeekdate().hashCode());
        result = prime * result + ((getMonthcount() == null) ? 0 : getMonthcount().hashCode());
        result = prime * result + ((getMonthtotal() == null) ? 0 : getMonthtotal().hashCode());
        result = prime * result + ((getMonthdate() == null) ? 0 : getMonthdate().hashCode());
        result = prime * result + ((getYearcount() == null) ? 0 : getYearcount().hashCode());
        result = prime * result + ((getYeartotal() == null) ? 0 : getYeartotal().hashCode());
        result = prime * result + ((getYeardate() == null) ? 0 : getYeardate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", count=").append(count);
        sb.append(", total=").append(total);
        sb.append(", date=").append(date);
        sb.append(", daycount=").append(daycount);
        sb.append(", daytotal=").append(daytotal);
        sb.append(", daydate=").append(daydate);
        sb.append(", weekcount=").append(weekcount);
        sb.append(", weektotal=").append(weektotal);
        sb.append(", weekdate=").append(weekdate);
        sb.append(", monthcount=").append(monthcount);
        sb.append(", monthtotal=").append(monthtotal);
        sb.append(", monthdate=").append(monthdate);
        sb.append(", yearcount=").append(yearcount);
        sb.append(", yeartotal=").append(yeartotal);
        sb.append(", yeardate=").append(yeardate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}