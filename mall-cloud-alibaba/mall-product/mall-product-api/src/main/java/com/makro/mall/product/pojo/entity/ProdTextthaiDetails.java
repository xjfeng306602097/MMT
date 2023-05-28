package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 导入模板的数据
 *
 * @TableName PROD_TEXTTHAI_DETAILS
 */
@TableName(value = "PROD_TEXTTHAI_DETAILS")
@Data
public class ProdTextthaiDetails extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 商品编码
     */
    private String article;

    /**
     * 商品名
     */
    private String userDesc;

    /**
     * 描述信息
     */
    private String detail;

    /**
     * 1个商品价格
     */
    private String step1;

    /**
     * 促销价
     */
    private String promo;

    /**
     * 包装单位
     */
    private String unitPack;

    /**
     * 价格单位
     */
    private String priceUnit;

    /**
     * 折扣
     */
    private String priceOff;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 导入文件ID
     */
    private String infoid;

    /**
     * 类别编码
     */
    private Long categoryid;

    /**
     * 主表UUID
     */
    private String dataid;

    /**
     * 0: 无效, 1: 有效
     */
    private int isvalid;

    /**
     *
     */
    private String mmCode;

    /**
     *
     */
    private String icon1;

    /**
     *
     */
    private String icon2;

    /**
     *
     */
    private String icon3;

    /**
     *
     */
    private String iconRemark;

    /**
     * N元N件
     */
    private String step2;

    /**
     *
     */
    private String step2unit;

    /**
     * N元N件
     */
    private String step3;

    /**
     *
     */
    private String step3unit;

    /**
     * N元N件
     */
    private String step4;

    /**
     *
     */
    private String step4unit;

    /**
     *
     */
    private String userRemark1;

    /**
     *
     */
    private String userRemark2Theme;

    /**
     *
     */
    private String userPage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", type=").append(channelType);
        sb.append(", article=").append(article);
        sb.append(", desc=").append(userDesc);
        sb.append(", detail=").append(detail);
        sb.append(", step1=").append(step1);
        sb.append(", promo=").append(promo);
        sb.append(", unitPack=").append(unitPack);
        sb.append(", priceUnit=").append(priceUnit);
        sb.append(", priceOff=").append(priceOff);
        sb.append(", creator=").append(creator);
        sb.append(", infoid=").append(infoid);
        sb.append(", categoryid=").append(categoryid);
        sb.append(", dataid=").append(dataid);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", isvalid=").append(isvalid);
        sb.append(", mmCode=").append(mmCode);
        sb.append(", icon1=").append(icon1);
        sb.append(", icon2=").append(icon2);
        sb.append(", icon3=").append(icon3);
        sb.append(", iconRemark=").append(iconRemark);
        sb.append(", step2=").append(step2);
        sb.append(", step2unit=").append(step2unit);
        sb.append(", step3=").append(step3);
        sb.append(", step3unit=").append(step3unit);
        sb.append(", step4=").append(step4);
        sb.append(", step4unit=").append(step4unit);
        sb.append(", userRemark1=").append(userRemark1);
        sb.append(", userRemark2Theme=").append(userRemark2Theme);
        sb.append(", userPage=").append(userPage);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}