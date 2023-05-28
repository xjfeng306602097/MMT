package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.message.AudioMessage;
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
public class AudioContentStrategy implements LineEvent<MessageEvent<AudioMessageContent>> {

    private final LineService lineService;
    private final LineSDK lineSDK;


    @Override
    public LineEvent<MessageEvent<AudioMessageContent>> handle(MessageEvent<AudioMessageContent> event) {
        final ContentProvider provider = event.getMessage().getContentProvider();
        lineSDK.reply(event.getReplyToken(), new AudioMessage(provider.getOriginalContentUrl(), 100L));
        return this;
    }
}
