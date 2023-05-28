package com.makro.mall.stat.pojo.metadata;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName page_view_log
 */
@TableName(value = "page_view_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageViewLog implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 产生时间戳
     */
    private Date ts;
    /**
     * 日志事件类型
     */
    private String event;
    /**
     * uuid,设备唯一标识，
     */
    private String uuid;
    /**
     * 业务id
     */
    @TableField(value = "bizId")
    private String bizId;
    /**
     * user-agent信息
     */
    @TableField(value = "userAgent")
    private String userAgent;
    /**
     * 是否为移动平台, 0-否，1-是
     */
    private Integer mobile;
    /**
     * 浏览器类型
     */
    private String browser;
    /**
     * 浏览器版本
     */
    @TableField(value = "browserVersion")
    private String browserVersion;
    /**
     * 平台类型
     */
    private String platform;
    /**
     * 系统类型
     */
    private String os;
    /**
     * 系统版本
     */
    @TableField(value = "osVersion")
    private String osVersion;
    /**
     * 引擎类型
     */
    private String engine;
    /**
     * 引擎版本
     */
    @TableField(value = "engineVersion")
    private String engineVersion;
    /**
     * referer
     */
    private String referer;
    /**
     * 累计点击
     */
    @TableField(value = "totalCount")
    private Long totalCount;
    /**
     * 事件时间
     */
    @TableField(value = "eventDate")
    private Date eventDate;
    /**
     * 会员账号
     */
    @TableField(value = "memberNo")
    private String memberNo;
    /**
     * 会员类型
     */
    @TableField(value = "memberType")
    private String memberType;
    /**
     * MM活动编码
     */
    @TableField(value = "mmCode")
    private String mmCode;
    /**
     * 店铺code
     */
    @TableField(value = "storeCode")
    private String storeCode;
    /**
     * 渠道,email/sms/line/facebook/app
     */
    private String channel;
    /**
     * 类型,h5/pdf
     */
    @TableField(value = "publishType")
    private String publishType;
    /**
     * 页码
     */
    @TableField(value = "pageNo")
    private String pageNo;
    /**
     * url地址
     */
    @TableField(value = "pageUrl")
    private String pageUrl;
    /**
     * 是否是新的请求
     */
    @TableField(value = "isNew")
    private Integer isNew;
    /**
     * 页面类型
     */
    @TableField(value = "pageType")
    private String pageType;

}