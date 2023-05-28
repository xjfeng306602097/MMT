package com.makro.mall.stat.pojo.snapshot;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName behavior_data
 */
@TableName(value = "behavior_data")
@Data
@ExcelIgnoreUnannotated
public class BehaviorData implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String mmCode;

    @ExcelProperty(value = "Date", index = 0)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd")
    private Date date;

    @ExcelProperty(value = "Total visits", index = 1)
    @ColumnWidth(20)
    private Long pv;

    @ExcelProperty(value = "Unique Visits", index = 2)
    @ColumnWidth(20)
    private Long uv;

    @ExcelProperty(value = "New Unique visits", index = 3)
    @ColumnWidth(30)
    private Long newUv;

    private Long mv;

}