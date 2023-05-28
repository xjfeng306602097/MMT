package com.makro.mall.stat.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
public class SummaryOfClicksDetailVO {

    @ExcelProperty({"Page Click Summary(Only shows if you choose a specific MM)", "Page Name"})
    @ColumnWidth(20)
    private Integer pageNo;
    @ExcelProperty({"Page Click Summary(Only shows if you choose a specific MM)", "Total View"})
    @ColumnWidth(20)
    private Long totalView;
    private List<String> mostItem;
    private String mostItemClick;
    private List<String> leastItem;
    private String leastItemClick;
    @ExcelProperty({"Page Click Summary(Only shows if you choose a specific MM)", "Total Clicks"})
    @ColumnWidth(20)
    private Long totalClicks;
}
