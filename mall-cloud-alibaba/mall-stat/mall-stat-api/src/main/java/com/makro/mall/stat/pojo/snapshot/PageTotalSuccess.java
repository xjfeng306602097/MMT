package com.makro.mall.stat.pojo.snapshot;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName page_total_success
 */
@TableName(value ="page_total_success")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PageTotalSuccess implements Serializable {
    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * 从 MM_ACTIVITY拿
     */
    private String mmCode;

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
     * Send total 取MM_PUBLISH_JOB_TASK_LOG count()
     */
    private Long total;

    /**
     * Send success 取MM_PUBLISH_JOB_TASK_LOG count() where STATUS = 2
     */
    private Long success;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}