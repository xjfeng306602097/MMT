package com.makro.mall.message.pojo.entity;

import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.message.enums.SmsChannelEnum;
import com.makro.mall.message.enums.SmsTypeEnum;
import com.makro.mall.mongo.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 邮件数据
 * @date 2021/11/3
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document("sms_message")
public class SmsMessage extends BaseEntity {

    public static final Long MAIL_SEND_DELAY_THRESHOLD = 86400L;
    /**
     * 对接渠道
     */
    private SmsChannelEnum channelEnum;

    /**
     * 发送者
     */
    private String sender = "Makro";

    /**
     * 收件人邮箱
     */
    @Indexed
    private List<String> receivers = new ArrayList<String>();

    @ApiModelProperty("客户Id")
    private Long customerId;

    /**
     * 内容
     */
    private String msg;

    /**
     * 短信发送类型
     */
    private SmsTypeEnum smsTypeEnum;


    //    @ApiModelProperty(value = "状态,0-待发送，1-发送成功,2-部分发送成功,3-失败", required = true)
    @ApiModelProperty(value = "0-Not sent 1-Failed 2-Succeeded 3-Canceled")
    private MessageSendEnum status;

    @CreatedBy
    private String creator;

    @LastModifiedBy
    private String lastUpdater;

    @ApiModelProperty(value = "发送sms时间")
    private Long sendTime;

    private Integer successCount = 0;

    @ApiModelProperty(value = "是否删除,1-已删除，0-未删除", required = true)
    private Integer isDelete;

    @ApiModelProperty(value = "延期执行时间,单位为秒,默认为0表示立即执行发送", required = false)
    private Long delay = 0L;

    @ApiModelProperty(value = "sms其他附带内容", required = false)
    private JSONObject smsContent;

    @ApiModelProperty(value = "bizId,用于外部查询的业务键", required = true)
    @Indexed
    private String bizId;

    @ApiModelProperty(value = "bizId,用于内部业务查询的业务键", required = true)
    @Indexed
    private String bizInnerId;
    @Indexed
    private Long mmPublishJobSmsTaskId;

}
