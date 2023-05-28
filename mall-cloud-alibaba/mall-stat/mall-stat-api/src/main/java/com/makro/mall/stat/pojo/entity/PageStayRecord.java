package com.makro.mall.stat.pojo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author xiaojunfeng
 * @TableName page_stay_log
 */
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageStayRecord extends AbstractLogRecord implements Serializable {

    /**
     * 会员账号
     */
    @ApiModelProperty(value = "会员号")
    private String memberNo;

    /**
     * MM活动编码
     */
    @ApiModelProperty(value = "MM活动编码")
    private String mmCode;

    /**
     * 店铺code
     */
    @ApiModelProperty(value = "店铺code")
    private String storeCode;

    /**
     * 渠道,
     * email/sms/line/facebook/app
     */
    @ApiModelProperty(value = "渠道 email/sms/line/facebook/app")
    private String channel;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码")
    private String pageNo;

    /**
     * 是否是新的请求
     */
    private Integer isNew;

    /**
     * 停留时间，ms为单位
     */
    @ApiModelProperty(value = "停留时间，ms为单位")
    private Long stayTime;

    /**
     * 页面类型
     */
    @ApiModelProperty(value = "页面类型")
    private String pageType;

    /**
     * 商品类型
     */
    @ApiModelProperty(value = "商品类型")
    private String goodsType;


    public void addStayTime(Long time) {
        this.stayTime = this.stayTime + time;
    }

}