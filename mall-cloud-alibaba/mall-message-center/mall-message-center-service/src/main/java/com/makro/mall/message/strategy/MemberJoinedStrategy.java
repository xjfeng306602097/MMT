package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MemberJoinedEvent;
import com.linecorp.bot.model.event.source.Source;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.message.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author jincheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberJoinedStrategy implements LineEvent<MemberJoinedEvent> {

    private final LineService lineService;
    private final LineSDK lineSDK;

    @Override
    public LineEvent<MemberJoinedEvent> handle(MemberJoinedEvent event) {
        String replyToken = event.getReplyToken();
        lineSDK.reply(replyToken, "Got memberJoined message " + event.getJoined().getMembers()
                .stream().map(Source::getUserId)
                .collect(Collectors.joining(",")));
        return this;
    }
}
