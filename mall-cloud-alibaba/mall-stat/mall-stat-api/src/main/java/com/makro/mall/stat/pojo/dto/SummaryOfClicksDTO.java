package com.makro.mall.stat.pojo.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ExcelIgnoreUnannotated
@AllArgsConstructor
@NoArgsConstructor
public class SummaryOfClicksDTO {
    @ExcelProperty("Item")
    private String name;
    @ExcelProperty("Click")
    private String value;

    private List<String> list;
    @ExcelProperty("Item/Page")
    private String itemOrPage;
}
