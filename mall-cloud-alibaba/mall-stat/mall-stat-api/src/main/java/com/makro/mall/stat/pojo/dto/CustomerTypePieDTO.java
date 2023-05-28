package com.makro.mall.stat.pojo.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 功能描述:数据分析
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/23 数据分析
 */
@Data
@ExcelIgnoreUnannotated
public class CustomerTypePieDTO {
    @ExcelProperty("Customer Type")
    @ColumnWidth(20)
    private String name;
    @ExcelProperty("Summary")
    @ColumnWidth(20)
    private Long value;
    @ExcelProperty("Rate")
    @ColumnWidth(10)
    private Double rate;


}
