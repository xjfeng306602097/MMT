package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description admin 00
 * file 01
 * auth 02
 * message 03
 * @date 2022/5/12
 */
public class MessageStatusCode extends StatusCode {

    public static final StatusCode HAVE_NO_RECEIVERS;
    public static final StatusCode GET_LINE_MESSAGE_TYPE_ERROR;
    public static final StatusCode REPLYTOKEN_MUST_NOT_BE_EMPTY;
    public static final StatusCode TO_MUST_NOT_BE_EMPTY;
    public static final StatusCode ALREADY_UNSUBSCRIBED;


    static {
        HAVE_NO_RECEIVERS = new StatusCode("0301", "have.no.receivers", Level.WARN);
        GET_LINE_MESSAGE_TYPE_ERROR = new StatusCode("0302", "get.line.message.type.error", Level.WARN);
        REPLYTOKEN_MUST_NOT_BE_EMPTY = new StatusCode("0303", "replyToken.must.not.be.empty", Level.WARN);
        TO_MUST_NOT_BE_EMPTY = new StatusCode("0304", "to.must.not.be.empty", Level.WARN);
        ALREADY_UNSUBSCRIBED = new StatusCode("0305", "already.unsubscribed", Level.WARN);
    }


    public MessageStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }


    public MessageStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }


}
