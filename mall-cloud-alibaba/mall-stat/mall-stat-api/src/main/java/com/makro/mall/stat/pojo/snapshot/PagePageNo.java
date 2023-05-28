package com.makro.mall.stat.pojo.snapshot;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jincheng
 * @TableName page_page_no
 */
@TableName(value = "page_page_no")
@Data
@ExcelIgnoreUnannotated
public class PagePageNo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String mmCode;
    private String channel;

    /**
     * 页码
     */
    @ExcelProperty({"Page Click Summary", "Page Name"})
    @ColumnWidth(20)
    private String pageNo;

    /**
     * 浏览次数
     */
    private Long count;
    @ExcelProperty({"Page Click Summary", "Click Counts"})
    @ColumnWidth(20)
    private Long pv;

    private Long uv;

    private Date date;

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
        PagePageNo other = (PagePageNo) that;
        return (this.getMmCode() == null ? other.getMmCode() == null : this.getMmCode().equals(other.getMmCode()))
                && (this.getPageNo() == null ? other.getPageNo() == null : this.getPageNo().equals(other.getPageNo()))
                && (this.getPv() == null ? other.getPv() == null : this.getPv().equals(other.getPv()))
                && (this.getUv() == null ? other.getUv() == null : this.getUv().equals(other.getUv()))
                && (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMmCode() == null) ? 0 : getMmCode().hashCode());
        result = prime * result + ((getPageNo() == null) ? 0 : getPageNo().hashCode());
        result = prime * result + ((getPv() == null) ? 0 : getPv().hashCode());
        result = prime * result + ((getUv() == null) ? 0 : getUv().hashCode());
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", mmCode=").append(mmCode);
        sb.append(", pageNo=").append(pageNo);
        sb.append(", pv=").append(pv);
        sb.append(", uv=").append(uv);
        sb.append(", date=").append(date);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}