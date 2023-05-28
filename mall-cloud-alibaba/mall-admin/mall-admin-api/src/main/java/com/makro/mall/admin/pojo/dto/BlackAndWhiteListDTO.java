package com.makro.mall.admin.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackAndWhiteListDTO {
    @ExcelProperty("code")
    @ApiModelProperty("万客隆客户号")
    private String code;
}
