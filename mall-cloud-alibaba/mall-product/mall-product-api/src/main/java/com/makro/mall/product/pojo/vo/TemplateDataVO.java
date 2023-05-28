package com.makro.mall.product.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description: 已作废
 * @Author: zhuangzikai
 * @Date: 2021/10/26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDataVO {

    /**
     * 第一列
     */
    private String firstColumn;

    /**
     * 采购员
     */

    private String buyerid;

    /**
     * JOINT
     */
    private String joint;

    /**
     * 其他海报
     */
    private String othermail;

    /**
     * 多件购买阶梯
     */
    private String multibuy;

    /**
     * 商品编码
     */
    private String itemcode;

    /**
     * 商品名
     */
    private String namethai;

    /**
     * 英文名称
     */
    private String nameeng;

    /**
     * 图片关联
     */
    private String photoref;

    /**
     * 原价
     */
    private BigDecimal normalprice;

    /**
     * 促销价
     */
    private BigDecimal promoprice;

    /**
     * 其他促销价
     */
    private BigDecimal promopriceeco;

    /**
     * 位置异常
     */
    private String locationexception;

    /**
     * 价格异常
     */
    private String priceexception;

    /**
     * 原备注
     */
    private String remark;

    /**
     * 利润
     */
    private String profitclassic;

    /**
     * 其他利润
     */
    private String profiteco;

    /**
     * 箱规
     */
    private Long unitpack;

    /**
     * 单品价格
     */
    private BigDecimal priceunit;

    /**
     * 销售量
     */
    private Long salesqty;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 总价
     */
    private BigDecimal price;

    /**
     * 让利
     */
    private BigDecimal profitlost;

    /**
     * 折扣比例
     */
    private BigDecimal margin;

    /**
     * 商城链接编码
     */
    private String urlparma;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 促销类型：1正常，2赠品，3组合
     */
    private Integer promotype;

    /**
     * 销售单位
     */
    private String saleunit;

    /**
     * 规格
     */
    private String pack;

    /**
     * 描述
     */
    private String description;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * N件M元
     */
    private Long qty2;

    private String qty2unit;

    /**
     * N件M元
     */
    private BigDecimal promoprice2;

    private String promoprice2description;

    /**
     * N件M元
     */
    private Long qty3;

    private String qty3unit;

    /**
     * N件M元
     */
    private BigDecimal promoprice3;

    private String promoprice3description;

    /**
     * N件M元
     */
    private Long qty4;

    private String qty4unit;

    /**
     * N件M元
     */
    private BigDecimal promoprice4;

    private String promoprice4description;

    /**
     * N件M元
     */
    private Long qty5;

    private String qty5unit;

    /**
     * N件M元
     */
    private BigDecimal promoprice5;

    private String promoprice5description;

    private BigDecimal avgprice1;

    private BigDecimal avgprice2;

    private BigDecimal avgprice3;

    private String taste;

    private String mutiitemcode;

    private String structuralproportion;

    /**
     * 促销描述
     */
    private String promotedesc;

    /**
     * 折扣金额
     */
    private BigDecimal discountamount;

    /**
     * 折扣百分比值
     */
    private BigDecimal discountpercent;

    /**
     * 赠品编码
     */
    private String giftitemno;

    /**
     * 备注1
     */
    private String remark1;

    /**
     * 备注2
     */
    private String remark2;

    /**
     * 备注3
     */
    private String remark3;

    /**
     * 图标1
     */
    private String icon1;

    /**
     * 图标2
     */
    private String icon2;

    /**
     * 图标3
     */
    private String icon3;

    /**
     * 规格，20升填20
     */
    private String unittitle1;

    /**
     * 规格，20升填升
     */
    private String unitvalue1;

    /**
     * 规格，20升填20
     */
    private String unittitle2;

    /**
     * 规格，20升填升
     */
    private String unitvalue2;

}
