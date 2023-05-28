package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.PostbackEvent;
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
public class PostbackStrategy implements LineEvent<PostbackEvent> {

    private final LineService lineService;
    private final LineSDK lineSDK;

    @Override
    public LineEvent<PostbackEvent> handle(PostbackEvent event) {
        String replyToken = event.getReplyToken();
        lineSDK.reply(replyToken,
                "Got postback data " + event.getPostbackContent().getData() + ", param " + event
                        .getPostbackContent().getParams().toString());
        return this;
    }
}
