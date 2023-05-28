package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.JoinEvent;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.message.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author jincheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JoinStrategy implements LineEvent<JoinEvent> {

    private final LineService lineService;
    private final LineSDK lineSDK;

    @Override
    public LineEvent<JoinEvent> handle(JoinEvent event) {
        String replyToken = event.getReplyToken();
        lineSDK.reply(replyToken, "Joined " + event.getSource());
        return this;
    }
}
