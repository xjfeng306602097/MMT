package com.makro.mall.stat.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @TableName product_analysis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAnalysisDTO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String mmCode;
    /**
     * 商品名称 英文名
     */
    @ApiModelProperty("英文名")
    private String nameEn;
    /**
     * 商品名称 泰文
     */
    @ApiModelProperty("泰文")
    private String nameThai;
    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码")
    @ExcelProperty({"Item Click Summary", "ItemCode"})
    @ColumnWidth(20)
    private String goodsCode;

    @ApiModelProperty("页码")
    private String pageNo;

    @ApiModelProperty("渠道")
    private List<String> channel;
}