package com.makro.mall.message.pojo.entity;

import com.makro.mall.mongo.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/1/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("mail_subscription_log")
public class MailSubscriptionLog extends BaseEntity {

    private String address;

    private String operation;


    /**
     * 业务id
     */
    private String bizId;

    /**
     * ip
     */
    private String ip;

    /**
     * user-agent信息
     */
    private String userAgent;

    /**
     * 浏览器类型
     */
    private String browser;
    /**
     * 浏览器版本
     */
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
    private String osVersion;

    /**
     * 引擎类型
     */
    private String engine;
    /**
     * 引擎版本
     */
    private String engineVersion;

    /**
     * referer
     */
    private String referer;

    /**
     * mobile
     */
    private boolean mobile;

    /**
     * 原因-英文
     */
    private String reason;

    /**
     * 原因-备注
     */
    private String remark;

}

