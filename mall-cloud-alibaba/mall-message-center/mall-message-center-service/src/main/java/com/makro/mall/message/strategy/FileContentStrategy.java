package com.makro.mall.message.strategy;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.message.TextMessage;
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
public class FileContentStrategy implements LineEvent<MessageEvent<FileMessageContent>> {

    private final LineService lineService;
    private final LineSDK lineSDK;

    @Override
    public LineEvent<MessageEvent<FileMessageContent>> handle(MessageEvent<FileMessageContent> event) {
        lineSDK.reply(event.getReplyToken(),
                new TextMessage(String.format("Received '%s'(%d bytes)",
                        event.getMessage().getFileName(),
                        event.getMessage().getFileSize())));
        return this;
    }
}
