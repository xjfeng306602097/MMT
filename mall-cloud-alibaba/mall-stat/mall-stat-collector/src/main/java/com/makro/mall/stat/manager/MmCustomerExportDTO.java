package com.makro.mall.stat.manager;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author jincheng
 */
@Data
@ExcelIgnoreUnannotated
public class MmCustomerExportDTO {
    @ExcelProperty("Communication Name")
    @ColumnWidth(30)
    private String communicationName;

    @ExcelProperty("Channel")
    @ColumnWidth(10)
    private String channel;

    @ExcelProperty("Mobile Phone")
    @ColumnWidth(20)
    private String mobilePhone;


    @ExcelProperty("Total Clicks")
    @ColumnWidth(10)
    private Long totalClicks;

    @ExcelProperty("Item Clicks")
    @ColumnWidth(20)
    private String itemClicks;

    @ExcelProperty("Customer type")
    @ColumnWidth(20)
    private String customerType;

    @ExcelProperty("Member id")
    @ColumnWidth(20)
    private String memberId;

    @ExcelProperty("Page no.")
    @ColumnWidth(10)
    private String pageNo;

    @ExcelProperty("Page stay time (Sec)")
    @ColumnWidth(20)
    private BigDecimal pageStayTime;

    private Long customerId;
}
