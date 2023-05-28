/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.makro.mall.message.callback;

import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.makro.mall.message.service.LineService;
import com.makro.mall.message.strategy.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jincheng
 */
@Slf4j
@LineMessageHandler
@RequiredArgsConstructor
public class LineCallback {

    private final LineService lineService;
    private final TextContentStrategy textMessageContentStrategy;
    private final StickerContentStrategy stickerMessageContentStrategy;
    private final LocationContentStrategy locationMessageContentStrategy;
    private final ImageContentStrategy imageMessageContentStrategy;
    private final AudioContentStrategy audioMessageContentStrategy;
    private final VideoContentStrategy videoMessageContentStrategy;
    private final FileContentStrategy fileMessageContentStrategy;
    private final VideoPlayCompleteStrategy videoPlayCompleteStrategy;
    private final UnfollowStrategy unfollowStrategy;
    private final UnknownStrategy unknownStrategy;
    private final FollowStrategy followStrategy;
    private final JoinStrategy joinStrategy;
    private final PostbackStrategy postbackStrategy;
    private final BeaconStrategy beaconStrategy;
    private final MemberJoinedStrategy memberJoinedStrategy;
    private final MemberLeftStrategy memberLeftStrategy;
    private final UnsendStrategy unsendStrategy;


    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        lineService.saveReceive(event);
        textMessageContentStrategy.handle(event);
    }

    @EventMapping
    public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
        lineService.saveReceive(event);
        stickerMessageContentStrategy.handle(event);
    }

    @EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
        lineService.saveReceive(event);
        locationMessageContentStrategy.handle(event);
    }

    @EventMapping
    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {
        lineService.saveReceive(event);
        imageMessageContentStrategy.handle(event);
    }

    @EventMapping
    public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) {
        lineService.saveReceive(event);
        audioMessageContentStrategy.handle(event);
    }

    @EventMapping
    public void handleVideoMessageEvent(MessageEvent<VideoMessageContent> event) {
        lineService.saveReceive(event);
        videoMessageContentStrategy.handle(event);
    }

    @EventMapping
    public void handleFileMessageEvent(MessageEvent<FileMessageContent> event) {
        lineService.saveReceive(event);
        fileMessageContentStrategy.handle(event);
    }


    @EventMapping
    public void handleVideoPlayCompleteEvent(VideoPlayCompleteEvent event) {
        lineService.saveReceive(event);
        videoPlayCompleteStrategy.handle(event);
    }


    @EventMapping
    public void handleUnfollowEvent(UnfollowEvent event) {
        lineService.saveReceive(event);
        unfollowStrategy.handle(event);
    }

    @EventMapping
    public void handleUnknownEvent(UnknownEvent event) {
        lineService.saveReceive(event);
        unknownStrategy.handle(event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        lineService.saveReceive(event);
        followStrategy.handle(event);
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        lineService.saveReceive(event);
        joinStrategy.handle(event);
    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) {
        lineService.saveReceive(event);
        postbackStrategy.handle(event);
    }

    @EventMapping
    public void handleBeaconEvent(BeaconEvent event) {
        lineService.saveReceive(event);
        beaconStrategy.handle(event);
    }

    @EventMapping
    public void handleMemberJoined(MemberJoinedEvent event) {
        lineService.saveReceive(event);
        memberJoinedStrategy.handle(event);
    }

    @EventMapping
    public void handleMemberLeft(MemberLeftEvent event) {
        lineService.saveReceive(event);
        memberLeftStrategy.handle(event);
    }

    @EventMapping
    public void handleMemberLeft(UnsendEvent event) {
        lineService.saveReceive(event);
        unsendStrategy.handle(event);
    }

    @EventMapping
    public void handleOtherEvent(Event event) {
        lineService.saveReceive(event);
        log.error("Received message(Ignored): {}", event);
    }
}
