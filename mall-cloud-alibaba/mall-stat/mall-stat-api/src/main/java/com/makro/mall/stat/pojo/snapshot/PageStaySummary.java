package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.makro.mall.stat.pojo.constant.CustomerTypeEnum;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName page_stay_summary
 */
@TableName(value = "page_stay_summary")
@Data
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageStaySummary implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String mmCode;
    /**
     * 页码
     */
    private String pageNo;
    /**
     * 时间
     */
    private String calHour;
    /**
     * 停留时间，ms为单位
     */
    private Long stayTime;
    /**
     * 访问次数
     */
    private Long visits;
    /**
     * 访问人数
     */
    private Long visitors;
    /**
     * 访问新客
     */
    private Long newVisitors;
    /**
     * 会员类型
     */
    private String memberType;
    /**
     *
     */
    private boolean fr;
    /**
     *
     */
    private boolean nfr;
    /**
     *
     */
    private boolean ho;
    /**
     *
     */
    private boolean sv;
    /**
     *
     */
    private boolean dt;
    /**
     *
     */
    private boolean ot;

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
     *
     */
    private Date calDate;
    /**
     *
     */
    private Date createTime;

    /**
     * 符合bounceRate的统计数量
     */
    private Long bounceRateCounts;

    public String getCustomerType() {
        if (isDt()) {
            return CustomerTypeEnum.DT.getName();
        }
        if (isFr()) {
            return CustomerTypeEnum.FR.getName();
        }
        if (isHo()) {
            return CustomerTypeEnum.HO.getName();
        }
        if (isOt()) {
            return CustomerTypeEnum.OT.getName();
        }
        if (isNfr()) {
            return CustomerTypeEnum.NFR.getName();
        }
        if (isSv()) {
            return CustomerTypeEnum.SV.getName();
        }
        return null;
    }
    
}