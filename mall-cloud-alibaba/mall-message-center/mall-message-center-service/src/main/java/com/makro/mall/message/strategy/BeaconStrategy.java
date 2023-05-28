package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.BeaconEvent;
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
public class BeaconStrategy implements LineEvent<BeaconEvent> {

    private final LineService lineService;
    private final LineSDK lineSDK;


    @Override
    public LineEvent<BeaconEvent> handle(BeaconEvent event) {
        String replyToken = event.getReplyToken();
        lineSDK.reply(replyToken, "Got beacon message " + event.getBeacon().getHwid());
        return this;
    }
}
