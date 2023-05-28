package com.makro.mall.message.pojo.entity;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.makro.mall.message.enums.SmsChannelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;


@EqualsAndHashCode
@Data
@Document("message_properties")
public class MessageProperties {

    @Id
    protected String id;
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime gmtModified;
    private String lineBotChannelToken;
    private String liffid;
    //@ApiModelProperty("多发的api地址")
    //  private String smsMultipleUrl;
    // private String smsProjectId;
    // private String smsPwd;
    //private String smsSender;
    private SmsChannelEnum smsChannel;
    private Map<String, JSONObject> smsMap;
    private String mailUserName;
    private String mailPassWord;
    private String mailPort;
    private String mailHost;
    private String mailProtocol;
    private String mailAuth;
    private String mailConnectionTimeout;
    private String mailTimeout;
    private String mailProxyHost;
    private String mailProxyPort;
    private String mailApp;
    private String mailLimit;
    @LastModifiedBy
    private String lastUpdater;

}
