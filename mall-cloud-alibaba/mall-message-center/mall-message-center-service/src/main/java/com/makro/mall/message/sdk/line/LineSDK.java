package com.makro.mall.message.sdk.line;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MessageStatusCode;
import com.makro.mall.message.service.MessagePropertiesService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.singletonList;

/**
 * You have reached your monthly limit.
 * 600ms/条
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Getter
public class LineSDK {

    private final LineMessagingClient lineMessagingClient;
    private final MessagePropertiesService messagePropertiesService;

    @Value("${line.login.clientId}")
    public String loginClientId;
    @Value("${line.login.callbackUrl}")
    public String loginCallbackUrl;
    @Value("${line.login.channelSecret}")
    public String loginChannelSecret;


    /**
     * 功能描述:回复消息
     * https://developers.line.biz/en/reference/messaging-api/#send-reply-message
     *
     * @Date: 2022/7/26 Line
     */
    public void reply(String replyToken, String message) {
        Assert.notNull(replyToken.isEmpty(), MessageStatusCode.REPLYTOKEN_MUST_NOT_BE_EMPTY);
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        reply(replyToken, new TextMessage(message));
    }

    public void reply(String replyToken, Message message) {
        reply(replyToken, singletonList(message));
    }

    public void reply(String replyToken, List<Message> messages) {
        reply(replyToken, messages, false);
    }

    public boolean reply(String replyToken,
                         List<Message> messages,
                         boolean notificationDisabled) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
            log.info("reply messages: {}", apiResponse);

            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("reply messages error:{} messages: {} replyToken:{} notificationDisabled:{}", e, messages, replyToken, notificationDisabled);
            return false;
        }
    }

    /**
     * 功能描述:推送5人内消息
     * https://developers.line.biz/en/reference/messaging-api/#send-push-message
     *
     * @Date: 2022/7/26 Line
     */
    public boolean push(String to, Message message) {
        Assert.notNull(to.isEmpty(), MessageStatusCode.TO_MUST_NOT_BE_EMPTY);
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .pushMessage(new PushMessage(to, message))
                    .get();
            log.info("push messages: {}", apiResponse);

            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("push messages error:{} to: {} message:{}", e, to, message);
            return false;
        }
    }


    /**
     * 功能描述:推送多人消息
     *
     * @Date: 2022/7/26 Line
     */
    public boolean multicast(Set<String> to, Message message) {
        Assert.notNull(to.isEmpty(), MessageStatusCode.TO_MUST_NOT_BE_EMPTY);
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .multicast(new Multicast(to, message))
                    .get();
            log.info("multicast messages: {}", apiResponse);

            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("multicast messages error:{} to: {} message:{}", e, to, message);
            return false;
        }
    }


    public String multicastFlex(String message) {
        String body = HttpRequest.post("https://api.line.me/v2/bot/message/multicast")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + messagePropertiesService.findFirstById("1").getLineBotChannelToken())//头信息，多个头信息多次调用此方法即可
                .timeout(20000)
                .body(message)
                .execute().body();
        log.info("multicastFlex messages: {} body: {}", message, body);
        return body;
    }


    public String token(String code, String mmCode) {
        String body = HttpRequest.post("https://api.line.me/oauth2/v2.1/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(20000)
                .form("grant_type", "authorization_code")
                .form("code", code)
                .form("client_id", loginClientId)
                .form("client_secret", loginChannelSecret)
                .form("redirect_uri", loginCallbackUrl + "?mmCode=" + mmCode)
                .execute().body();
        log.info("token code: {} body: {}", code, body);
        JSONObject jsonObject = JSON.parseObject(body);
        String errorDescription = String.valueOf(jsonObject.get("error_description"));
        Assert.isTrue(Objects.equals("null", errorDescription), errorDescription);
        return jsonObject.get("id_token").toString();
    }

    public String verify(String token) {
        String body = HttpRequest.post("https://api.line.me/oauth2/v2.1/verify")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(20000)
                .form("id_token", token)
                .form("client_id", loginClientId)
                .execute().body();
        log.info("verify token: {} body: {}", token, body);
        JSONObject jsonObject = JSON.parseObject(body);
        return jsonObject.get("sub").toString();
    }

    public boolean broadcastFlex(String message) {
        String body = HttpRequest.post("https://api.line.me/v2/bot/message/broadcast")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + messagePropertiesService.findFirstById("1").getLineBotChannelToken())
                .timeout(20000)
                .body(message)
                .execute().body();
        log.info("broadcastFlex messages: {}body: {}", message, body);

        return true;
    }
}
