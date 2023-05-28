package com.makro.mall.admin.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述: 客户解析
 * 客户资料上传&Segment维护
 *
 * @Author: 卢嘉俊
 * @Date: 2022/5/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MmCustomerDTO {

    @ExcelProperty("name")
    @ApiModelProperty("客户名称")
    private String name;

    @ExcelProperty("phone")
    @ApiModelProperty("客户手机号")
    private String phone;

    @ExcelProperty("email")
    @ApiModelProperty("客户邮箱")
    private String email;

    @ExcelProperty("code")
    @ApiModelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("segment name")
    @ApiModelProperty("segment,用,隔开")
    private String segment;

    @ExcelProperty("line id")
    @ApiModelProperty("lineId")
    private String lineId;

    @ExcelProperty("memberType nameEn")
    @ApiModelProperty("memberTypeId")
    private String memberType;

}
