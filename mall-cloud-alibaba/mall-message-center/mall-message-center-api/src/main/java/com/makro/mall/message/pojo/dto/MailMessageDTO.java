package com.makro.mall.message.pojo.dto;

import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MessageStatusCode;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.message.enums.MailTypeEnum;
import io.swagger.annotations.ApiModelProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaojunfeng
 * @description 消息体
 * @date 2021/11/3
 */
public abstract class MailMessageDTO {

    private String id;

    private String sender = "default";

    /**
     * 收件人邮箱
     */
    private String[] toUser;

    /**
     * 抄送人邮箱
     */
    private String toCc;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 邮件发送类型
     */
    private MailTypeEnum mailTypeEnum;

    @ApiModelProperty(value = "mm发布任务id")
    private String mmPublishJobEmailTaskId;


    private boolean checkMailValid(String str) {
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(str);
        return m.matches();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String[] getToUser() {
        return toUser;
    }

    public void setToUser(String[] toUser) {
        Assert.isTrue(toUser != null && toUser.length > 0, StatusCode.TOUSER_NOT_NULL);
        for (String s : toUser) {
            Assert.isTrue(checkMailValid(s), MessageStatusCode.TOUSER_ADDRESS_FORMAT_ERROR);
        }
        this.toUser = toUser;
    }

    public String getToCc() {
        return toCc;
    }

    public void setToCc(String toCc) {
        this.toCc = toCc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MailTypeEnum getMailTypeEnum() {
        return mailTypeEnum;
    }

    public void setMailTypeEnum(MailTypeEnum mailTypeEnum) {
        this.mailTypeEnum = mailTypeEnum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMmPublishJobEmailTaskId() {
        return mmPublishJobEmailTaskId;
    }

    public void setMmPublishJobEmailTaskId(String mmPublishJobEmailTaskId) {
        this.mmPublishJobEmailTaskId = mmPublishJobEmailTaskId;
    }
}
