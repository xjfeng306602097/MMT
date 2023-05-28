package com.makro.mall.stat.pojo.metadata;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName app_uv_log
 */
@TableName(value = "app_uv_log")
@Data
public class AppUvLog implements Serializable {
    /**
     * 是否是新的请求
     */
    @TableField(value = "isNew")
    private Integer isNew;

    /**
     * 会员账号
     */
    @TableField(value = "memberNo")
    private String memberNo;
    @TableField(value = "memberType")
    private String memberType;

    /**
     * ip地址
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 产生时间戳
     */
    @TableField(value = "ts")
    private Date ts;

    /**
     * 日志事件类型
     */
    @TableField(value = "event")
    private String event;

    /**
     * uuid,设备唯一标识，
     */
    @TableField(value = "uuid")
    private String uuid;

    /**
     * user-agent信息
     */
    @TableField(value = "userAgent")
    private String userAgent;

    /**
     * 是否为移动平台, 0-否，1-是
     */
    @TableField(value = "mobile")
    private Integer mobile;

    /**
     * 浏览器类型
     */
    @TableField(value = "browser")
    private String browser;

    /**
     * 浏览器版本
     */
    @TableField(value = "browserVersion")
    private String browserVersion;

    /**
     * 平台类型
     */
    @TableField(value = "platform")
    private String platform;

    /**
     * 系统类型
     */
    @TableField(value = "os")
    private String os;

    /**
     * 系统版本
     */
    @TableField(value = "osVersion")
    private String osVersion;

    /**
     * 引擎类型
     */
    @TableField(value = "engine")
    private String engine;

    /**
     * 引擎版本
     */
    @TableField(value = "engineVersion")
    private String engineVersion;

    /**
     * referer
     */
    @TableField(value = "referer")
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}