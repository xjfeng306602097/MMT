package com.makro.mall.stat.pojo.snapshot;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName product_analysis
 */
@TableName(value = "product_analysis")
@Data
@ExcelIgnoreUnannotated
public class ProductAnalysis implements Serializable {
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
    @ExcelProperty({"Item Click Summary", "nameEn"})
    private String nameEn;
    /**
     * 商品名称 泰文
     */
    @ApiModelProperty("泰文")
    @ExcelProperty({"Item Click Summary", "nameTh"})
    private String nameThai;
    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码")
    @ExcelProperty({"Item Click Summary", "ItemCode"})
    @ColumnWidth(20)
    private String goodsCode;

    @ApiModelProperty("页码")
    @ExcelProperty({"Item Click Summary", "pageNo"})
    private String pageNo;

    @ApiModelProperty("渠道")
    @ExcelProperty({"Item Click Summary", "channel"})
    private String channel;
    /**
     * 点击次数
     */
    @ApiModelProperty("一个商品里的访问量总和")
    @ExcelProperty({"Item Click Summary", "Click Counts"})
    @ColumnWidth(20)
    private Long clicks;
    /**
     * 点击人数
     */
    @ApiModelProperty("一个商品里的访客数总和")
    private Long visitors;
    /**
     *
     */
    private Date date;
}