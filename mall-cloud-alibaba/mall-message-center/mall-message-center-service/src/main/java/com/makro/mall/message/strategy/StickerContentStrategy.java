package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.message.StickerMessage;
import com.makro.mall.message.sdk.line.LineSDK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StickerContentStrategy implements LineEvent<MessageEvent<StickerMessageContent>> {

    private final LineSDK lineSDK;


    @Override
    public LineEvent<MessageEvent<StickerMessageContent>> handle(MessageEvent<StickerMessageContent> event) {
        String replyToken = event.getReplyToken();
        StickerMessageContent content = event.getMessage();
        lineSDK.reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
        return this;
    }
}
