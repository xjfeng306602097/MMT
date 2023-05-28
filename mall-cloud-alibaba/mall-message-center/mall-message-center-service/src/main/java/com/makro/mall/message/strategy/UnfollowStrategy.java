package com.makro.mall.message.strategy;


import com.linecorp.bot.model.event.UnfollowEvent;
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
public class UnfollowStrategy implements LineEvent<UnfollowEvent> {
    private final LineService lineService;

    @Override
    public LineEvent<UnfollowEvent> handle(UnfollowEvent event) {
        log.info("unfollowed this bot: {}", event);
        return this;
    }
}