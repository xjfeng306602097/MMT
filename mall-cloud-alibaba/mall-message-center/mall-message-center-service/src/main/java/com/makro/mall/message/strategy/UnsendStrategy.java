package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.UnsendEvent;
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
public class UnsendStrategy implements LineEvent<UnsendEvent> {

    private final LineService lineService;


    @Override
    public LineEvent<UnsendEvent> handle(UnsendEvent event) {
        log.error("Got unSend event: {}", event);
        return this;
    }
}
