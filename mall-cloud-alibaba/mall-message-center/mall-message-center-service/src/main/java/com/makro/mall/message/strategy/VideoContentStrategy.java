package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.message.VideoMessage;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.message.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author jincheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VideoContentStrategy implements LineEvent<MessageEvent<VideoMessageContent>> {

    private final LineService lineService;
    private final LineSDK lineSDK;


    @Override
    public LineEvent<MessageEvent<VideoMessageContent>> handle(MessageEvent<VideoMessageContent> event) {
        log.info("Got video message: duration={}ms", event.getMessage().getDuration());

        // You need to install ffmpeg and ImageMagick.

        final ContentProvider provider = event.getMessage().getContentProvider();
        String trackingId = UUID.randomUUID().toString();
        log.info("Sending video message with trackingId={}", trackingId);
        lineSDK.reply(event.getReplyToken(),
                VideoMessage.builder()
                        .originalContentUrl(provider.getOriginalContentUrl())
                        .previewImageUrl(provider.getPreviewImageUrl())
                        .trackingId(trackingId)
                        .build());
        return this;
    }
}
