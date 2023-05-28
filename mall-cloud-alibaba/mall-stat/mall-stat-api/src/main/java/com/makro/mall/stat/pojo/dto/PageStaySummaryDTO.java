package com.makro.mall.stat.pojo.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/8/24
 */
@Data
@ExcelIgnoreUnannotated
public class PageStaySummaryDTO {

    @ExcelProperty("Page Name")
    @ColumnWidth(20)
    private String pageName;

    @ExcelProperty("Customer Type")
    @ColumnWidth(20)
    private String customerType;

    @ExcelProperty("Channel")
    @ColumnWidth(20)
    private String channel;

    @ExcelProperty("Average Stay")
    @ColumnWidth(20)
    private BigDecimal averageStay;

    @ExcelProperty("Bounce Rate")
    @ColumnWidth(20)
    private BigDecimal bounceRate;

    private Long visits;

    private Long stayTime;

    private Long bounceRateCounts;



}
