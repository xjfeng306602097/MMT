package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.VideoPlayCompleteEvent;
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
public class VideoPlayCompleteStrategy implements LineEvent<VideoPlayCompleteEvent> {

    private final LineService lineService;
    private final LineSDK lineSDK;

    @Override
    public LineEvent<VideoPlayCompleteEvent> handle(VideoPlayCompleteEvent event) {
        log.info("Got video play complete: tracking id={}", event.getVideoPlayComplete().getTrackingId());
        lineSDK.reply(event.getReplyToken(),
                "You played " + event.getVideoPlayComplete().getTrackingId());
        return this;
    }
}
