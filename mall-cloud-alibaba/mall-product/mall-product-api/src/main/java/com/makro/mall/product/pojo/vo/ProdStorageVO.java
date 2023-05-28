package com.makro.mall.product.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品库
 *
 * @TableName PROD_STORAGE
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdStorageVO implements Serializable {
    @ApiModelProperty("segmentName")
    String segmentName;

    @TableId
    private String id;

    @ExcelProperty("itemcode")
    @ApiModelProperty("商品编码")
    private String itemcode;

    @ExcelProperty("namethai")
    @ApiModelProperty("商品名")
    private String namethai;

    @ExcelProperty("nameen")
    @ApiModelProperty("英文名称")
    private String nameen;

    @ExcelProperty("unit")
    @ApiModelProperty("销售单位")
    private String qty1unit;

    @ExcelProperty("pack")
    @ApiModelProperty("规格")
    private String pack;

    @ExcelProperty("model")
    @ApiModelProperty("型号")
    private String model;

    @ExcelProperty("normalprice")
    private BigDecimal normalprice;

    @ExcelProperty("promoprice")
    private BigDecimal promoprice;

    @ApiModelProperty("导入判断是否存在")
    private Boolean isExist;

    @ApiModelProperty("isvalid")
    private Integer isvalid;

}