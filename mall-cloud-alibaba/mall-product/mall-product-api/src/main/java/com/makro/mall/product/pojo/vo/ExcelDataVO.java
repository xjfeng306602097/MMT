package com.makro.mall.product.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/04/14
 **/
@Data
public class ExcelDataVO {

    private String channelType;

    private String urlparam;

    private String namethai;

    private String nameen;

    private String pack;

    private String model;

    private BigDecimal normalprice;

    private BigDecimal promoprice;

    private String promotedesc;

    private Long qty1;

    private String qty1unit;

    private BigDecimal promoprice1;

    private Long qty2;

    private String qty2unit;

    private BigDecimal promoprice2;

    private String promoprice2description;

    private Long qty3;

    private String qty3unit;

    private BigDecimal promoprice3;

    private String promoprice3description;

    private Long qty4;

    private String qty4unit;

    private BigDecimal promoprice4;

    private String promoprice4description;

    private Integer page;

    private Integer sort;

    private String icon1;

    private String icon2;

    private String icon3;

    private String iconRemark;

    private Integer promotype;

    private String linkitemno;

    private String remark1;

    private String remark2;

    private String remark3;

    private String picid;

    private Integer row;

    @ExcelProperty("Product ID")
    private Long productId;


}
