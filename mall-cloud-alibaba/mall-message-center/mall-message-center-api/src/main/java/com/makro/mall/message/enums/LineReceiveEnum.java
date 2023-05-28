package com.makro.mall.message.enums;

import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.*;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MessageStatusCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/7/26 Line
 */
@AllArgsConstructor
@Slf4j
public enum LineReceiveEnum {
    TEXT_MESSAGE_CONTENT("TextMessageContent"),
    STICKER_MESSAGE_CONTENT("StickerMessageContent"),
    LOCATION_MESSAGE_CONTENT("LocationMessageContent"),
    IMAGE_MESSAGE_CONTENT("ImageMessageContent"),
    AUDIO_MESSAGE_CONTENT("AudioMessageContent"),
    VIDEO_MESSAGE_CONTENT("VideoMessageContent"),
    FILE_MESSAGE_CONTENT("FileMessageContent"),
    VIDEO_PLAY_COMPLETE_EVENT("VideoPlayCompleteEvent"),
    UNFOLLOW_EVENT("UnfollowEvent"),
    UNKNOWN_EVENT("UnknownEvent"),
    FOLLOW_EVENT("FollowEvent"),
    JOIN_EVENT("JoinEvent"),
    POSTBACK_EVENT("PostbackEvent"),
    BEACON_EVENT("BeaconEvent"),
    MEMBER_JOINED_EVENT("MemberJoinedEvent"),
    MEMBER_LEFT_EVENT("MemberLeftEvent"),
    UNSEND_EVENT("UnsendEvent"),
    OTHER_EVENT("OtherEvent");

    String name;


    public static <U extends MessageContent> LineReceiveEnum getLineMessageType(U message) {
        if (message instanceof TextMessageContent) {
            return TEXT_MESSAGE_CONTENT;
        } else if (message instanceof StickerMessageContent) {
            return STICKER_MESSAGE_CONTENT;
        } else if (message instanceof LocationMessageContent) {
            return LOCATION_MESSAGE_CONTENT;
        } else if (message instanceof ImageMessageContent) {
            return IMAGE_MESSAGE_CONTENT;
        } else if (message instanceof AudioMessageContent) {
            return AUDIO_MESSAGE_CONTENT;
        } else if (message instanceof VideoMessageContent) {
            return VIDEO_MESSAGE_CONTENT;
        } else if (message instanceof FileMessageContent) {
            return FILE_MESSAGE_CONTENT;
        } else {
            Assert.isTrue(true, MessageStatusCode.GET_LINE_MESSAGE_TYPE_ERROR);
            return null;
        }
    }

    public static LineReceiveEnum getType(Event event) {
        if (event instanceof VideoPlayCompleteEvent) {
            return VIDEO_PLAY_COMPLETE_EVENT;
        } else if (event instanceof UnfollowEvent) {
            return UNFOLLOW_EVENT;
        } else if (event instanceof UnknownEvent) {
            return UNKNOWN_EVENT;
        } else if (event instanceof FollowEvent) {
            return FOLLOW_EVENT;
        } else if (event instanceof JoinEvent) {
            return JOIN_EVENT;
        } else if (event instanceof PostbackEvent) {
            return POSTBACK_EVENT;
        } else if (event instanceof BeaconEvent) {
            return BEACON_EVENT;
        } else if (event instanceof MemberJoinedEvent) {
            return MEMBER_JOINED_EVENT;
        } else if (event instanceof MemberLeftEvent) {
            return MEMBER_LEFT_EVENT;
        } else if (event instanceof UnsendEvent) {
            return UNSEND_EVENT;
        } else {
            return OTHER_EVENT;
        }
    }
}
