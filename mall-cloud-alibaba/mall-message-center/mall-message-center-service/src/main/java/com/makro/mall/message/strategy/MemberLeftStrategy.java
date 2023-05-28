package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MemberLeftEvent;
import com.linecorp.bot.model.event.source.Source;
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
public class MemberLeftStrategy implements LineEvent<MemberLeftEvent> {

    private final LineService lineService;


    @Override
    public LineEvent<MemberLeftEvent> handle(MemberLeftEvent event) {
        log.info("Got memberLeft message: {}", event.getLeft().getMembers()
                .stream().map(Source::getUserId)
                .collect(Collectors.joining(",")));
        return this;
    }
}
