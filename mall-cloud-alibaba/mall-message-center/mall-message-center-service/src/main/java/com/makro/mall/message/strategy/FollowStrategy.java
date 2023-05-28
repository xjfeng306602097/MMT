package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.FollowEvent;
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
public class FollowStrategy implements LineEvent<FollowEvent> {

    private final LineService lineService;
    private final LineSDK lineSDK;


    @Override
    public LineEvent<FollowEvent> handle(FollowEvent event) {
        String replyToken = event.getReplyToken();
        lineSDK.reply(replyToken, "Got followed event");
        return this;
    }
}
