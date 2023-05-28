package com.makro.mall.message.pojo.entity;

import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.message.enums.LineReceiveEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author jincheng
 */
@Data
@Document("Line_receive_message")
public class LineReceiveMessage {

    LineReceiveEnum type;
    /**
     * Token for replying to this event.
     */
    String replyToken;


    String userId;

    String senderId;

    /**
     * Message body.
     */
    JSONObject message;

    /**
     * Time of the event.
     */
    Instant timestamp;

    /**
     * Channel state.
     * <dl>
     * <dt>active</dt>
     * <dd>The channel is active. You can send a reply message or push message from the bot server that received
     * this webhook event.</dd>
     * <dt>standby (under development)</dt>
     * <dd>The channel is waiting. The bot server that received this webhook event shouldn't send any messages.
     * </dd>
     * </dl>
     */
    String mode;

    LocalDateTime now = LocalDateTime.now();

}
