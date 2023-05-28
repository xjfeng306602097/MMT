package com.makro.mall.stat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author xiaojunfeng
 * @TableName page_stay_log
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageStayRecord extends AbstractLogRecord implements Serializable {

    /**
     * 会员账号
     */
    private String memberNo;

    /**
     * 会员类型
     */
    private String memberType;

    /**
     * MM活动编码
     */
    private String mmCode;

    /**
     * 店铺code
     */
    private String storeCode;

    /**
     * 渠道,
     * email/sms/line/facebook/app
     */
    private String channel;

    /**
     * 类型,
     * h5/pdf
     */
    private String publishType;

    /**
     * 页码
     */
    private String pageNo;

    /**
     * 是否是新的请求
     */
    private Integer isNew;

    /**
     * 停留时间，ms为单位
     */
    private Long stayTime;

    /**
     * 页面类型
     */
    private String pageType;

    /**
     * 商品类型
     */
    private String goodsType;

}