package com.makro.mall.message.strategy;


import com.linecorp.bot.model.event.UnknownEvent;
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
public class UnknownStrategy implements LineEvent<UnknownEvent> {
    private final LineService lineService;

    @Override
    public LineEvent<UnknownEvent> handle(UnknownEvent event) {
        log.info("UnknownEvent this bot: {}", event);
        return this;
    }
}