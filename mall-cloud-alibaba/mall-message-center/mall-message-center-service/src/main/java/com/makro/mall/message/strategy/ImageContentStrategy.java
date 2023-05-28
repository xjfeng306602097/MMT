package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
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
public class ImageContentStrategy implements LineEvent<MessageEvent<ImageMessageContent>> {

    private final LineService lineService;
    private final LineSDK lineSDK;

    @Override
    public LineEvent<MessageEvent<ImageMessageContent>> handle(MessageEvent<ImageMessageContent> event) {
        // You need to install ImageMagick
        final ContentProvider provider = event.getMessage().getContentProvider();
        lineSDK.reply(event.getReplyToken(),
                new ImageMessage(provider.getOriginalContentUrl(), provider.getPreviewImageUrl()));
        return this;
    }
}
