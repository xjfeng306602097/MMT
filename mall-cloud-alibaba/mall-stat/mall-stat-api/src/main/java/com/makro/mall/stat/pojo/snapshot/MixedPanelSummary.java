package com.makro.mall.stat.pojo.snapshot;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName mixed_panel_summary
 */
@TableName(value ="mixed_panel_summary")
@Data
@Accessors(chain = true)
public class MixedPanelSummary implements Serializable {
    private Date date;

    /**
     * 从 MM_ACTIVITY拿状态为六的
     */
    private String mmCode;

    /**
     * 页码 从mm_template拿
     */
    private String pageNo;

    /**
     * 1 CustomerTypeEnum 枚举类
     */
    private boolean fr;

    /**
     * 2 CustomerTypeEnum 枚举类
     */
    private boolean nfr;

    /**
     * 3 CustomerTypeEnum 枚举类
     */
    private boolean ho;

    /**
     * 4 CustomerTypeEnum 枚举类
     */
    private boolean sv;

    /**
     * 5 CustomerTypeEnum 枚举类
     */
    private boolean dt;

    /**
     * 6 CustomerTypeEnum 枚举类
     */
    private boolean ot;

    /**
     * 渠道,email/sms/line/facebook/app
     */
    private String channel;

    /**
     * 是否手机 0/1
     */
    private Boolean mobile;

    /**
     * 平台类型
     */
    private String platform;

    /**
     * Access to MM 取pv表count(DISTINCT uuid)
     */
    private Long memberToMm;

    private Long memberToMmPage;

    /**
     * Access to MM 取pv表count(DISTINCT Bizid)
     */
    private Long sessionToMm;

    private Long sessionToMmPage;

    /**
     * Access to Page 取pv表count()
     */
    private Long toPage;

    /**
     * Access to Item 取gc表count()
     */
    private Long toItem;

    /**
     * 取ps表的 sum(stay_time)
     */
    private Long stayTime;

    /**
     * 不知道取哪的计算
     */
    private Long bounceRateCounts;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}