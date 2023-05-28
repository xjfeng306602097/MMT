package com.makro.mall.stat.pojo.snapshot;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName page_channel_member_type
 */
@TableName(value = "page_channel_member_type")
@Data
@EqualsAndHashCode
@ToString
@ExcelIgnoreUnannotated
public class PageChannelMemberType implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String mmCode;
    /**
     *
     */
    @ExcelProperty(value = "Channel", index = 0)
    @ColumnWidth(20)
    private String channel;

    @ExcelProperty(value = "customerType", index = 1)
    @ColumnWidth(20)
    private String memberType;
    /**
     *
     */
    private boolean fr;
    /**
     *
     */
    private boolean nfr;
    /**
     *
     */
    private boolean ho;
    /**
     *
     */
    private boolean sv;
    /**
     *
     */
    private boolean dt;
    /**
     *
     */
    private boolean ot;
    /**
     *
     */
    private Long pv;
    /**
     *
     */
    @ExcelProperty(value = "Clicks", index = 2)
    @ColumnWidth(20)
    private Long uv;
    /**
     *
     */
    private Date date;
}