package com.makro.mall.stat.pojo.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class MostVisitPageDTO {


    @ExcelProperty(value = "Page Name", index = 0)
    @ColumnWidth(20)
    private String pageName;

    @ExcelProperty(value = "Summary", index = 1)
    @ColumnWidth(20)
    private Long summary;
}
