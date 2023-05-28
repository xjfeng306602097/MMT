package com.makro.mall.stat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/27
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractLogRecord {
    /**
     * ip地址
     */
    protected String ip;

    /**
     * 产生时间戳
     */
    protected Long ts;

    /**
     * 日志事件类型
     */
    protected String event;

    /**
     * uuid,设备唯一标识，
     */
    protected String uuid;

    /**
     * 业务id
     */
    protected String bizId;

    /**
     * user-agent信息
     */
    protected String userAgent;

    /**
     * 是否为移动平台, 0-否，1-是
     */
    protected Integer mobile;
    /**
     * 浏览器类型
     */
    protected String browser;
    /**
     * 浏览器版本
     */
    protected String browserVersion;

    /**
     * 平台类型
     */
    protected String platform;

    /**
     * 系统类型
     */
    protected String os;
    /**
     * 系统版本
     */
    protected String osVersion;

    /**
     * 引擎类型
     */
    protected String engine;
    /**
     * 引擎版本
     */
    protected String engineVersion;

    /**
     * referer
     */
    protected String referer;

    /**
     * 累计点击
     */
    protected Long totalCount;

    /**
     * 事件时间
     */
    protected Date eventDate = new Date();

    /**
     * 设备id
     */
    protected String deviceId;

}
