package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description admin 00
 * @date 2022/5/12
 */
public class AdminStatusCode extends StatusCode {

    //查无次客户消息
    public static final StatusCode CUSTOMER_IS_EMPTY;
    public static final StatusCode SEGMENT_ID_IS_EMPTY;
    public static final StatusCode THE_NUMBER_OF_CUSTOMERS_IS_MORE_THAN_20000;
    public static final StatusCode CUSTOMER_NAME_IS_EMPTY;//"名字不为空"
    public static final StatusCode CUSTOMER_PHONE_IS_EMPTY;

    public static final StatusCode CUSTOMER_EMAIL_IS_ILLEGAL;
    public static final StatusCode SEGMENT_NAME_IS_EMPTY;
    public static final StatusCode MMACTIVITY_IS_EMPTY;
    public static final StatusCode MMPUBLISHJOB_IS_EMPTY;
    public static final StatusCode MMPUBLISHJOB_REVIEW_FAILED;
    public static final StatusCode THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_EMAIL;

    public static final StatusCode THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_SMS;
    public static final StatusCode WORK_TIME_IS_BEFORE_NOW;
    public static final StatusCode CANNOT_OPEN_SEGMENT;
    public static final StatusCode THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_LINE;
    public static final StatusCode CURRENT_ROLE_NAME_OR_CODE_DUPLICATE;
    public static final StatusCode MENUID_PERMISSIONIDS_NOT_MATCH;
    public static final StatusCode CUSTOMER_IS_EXISTS;
    public static final StatusCode FIELD_ERROR;
    public static final StatusCode NAME_ALREADY_EXISTS;
    public static final StatusCode NOT_CONFIGURED_LINE_BOT_CHANNEL_TOKEN;
    public static final StatusCode THE_CURRENT_STATUS_CANNOT_BE_ROLLED_BACK;
    public static final StatusCode STORE_CODE_IS_NOT_EXISTS;
    public static final StatusCode ONLY_SUPPORTED_CLASSIC_OR_ECO;
    public static final StatusCode MEMBERTYPE_IS_NOT_EXISTS;
    public static final StatusCode TITLE_IS_NOT_NULL;
    public static final StatusCode SEGMENT_IS_NOT_EXISTS;
    public static final StatusCode CHANNELTYPE_ONLY_SUPPORTED_CLASSIC_OR_ECO;
    public static final StatusCode THE_NUMBER_OF_PROD_STORAGE_IS_MORE_THAN_20000;
    public static final StatusCode SAVE_MM_EXCEPTION;
    public static final StatusCode SAVE_BOUNCERATE_EXCEPTION;
    public static final StatusCode SAVE_PRODUCT_EXCEPTION;
    public static final StatusCode NO_SUCH_CHANNEL;
    public static final StatusCode CUSTOMER_LINE_IS_ILLEGAL;
    public static final StatusCode CAN_ONLY_BE_SENT_ONCE_IN_30S;
    public static final StatusCode NO_SENDABLE_CUSTOMERS;
    public static final StatusCode TEMPLATE_CONTENT_CANNOT_EXCEED_300;

    static {
        CUSTOMER_IS_EMPTY = new StatusCode("0036", "customer.is.empty", Level.WARN);
        SEGMENT_ID_IS_EMPTY = new StatusCode("0037", "segment.id.is.empty", Level.WARN);
        THE_NUMBER_OF_CUSTOMERS_IS_MORE_THAN_20000 = new StatusCode("0038", "the.number.of.customers.is.more.than.20000", Level.WARN);
        CUSTOMER_NAME_IS_EMPTY = new StatusCode("0039", "customer.name.is.empty", Level.WARN);
        CUSTOMER_PHONE_IS_EMPTY = new StatusCode("0040", "customer.phone.is.empty", Level.WARN);
        SEGMENT_NAME_IS_EMPTY = new StatusCode("0041", "segment.name.is.empty", Level.WARN);
        MMACTIVITY_IS_EMPTY = new StatusCode("0042", "mmactivity.is.empty", Level.WARN);
        MMPUBLISHJOB_IS_EMPTY = new StatusCode("0043", "mmpublishjob.is.empty", Level.WARN);
        MMPUBLISHJOB_REVIEW_FAILED = new StatusCode("0044", "mmpublishjob.review.is.empty", Level.WARN);
        THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_EMAIL = new StatusCode("0045", "this.mmpublishjob.can.not.send.by.email", Level.WARN);
        THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_SMS = new StatusCode("0046", "this.mmpublishjob.can.not.send.by.sms", Level.WARN);
        WORK_TIME_IS_BEFORE_NOW = new StatusCode("0047", "work.time.is.before.now", Level.WARN);
        CANNOT_OPEN_SEGMENT = new StatusCode("0048", "cannot.open.segment", Level.WARN);
        THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_LINE = new StatusCode("0049", "this.mmpublishjob.can.not.send.by.line", Level.WARN);
        CURRENT_ROLE_NAME_OR_CODE_DUPLICATE = new StatusCode("0050", "current.role.name.or.code.duplicate", Level.WARN);
        MENUID_PERMISSIONIDS_NOT_MATCH = new StatusCode("0051", "menuId.permissionIds.not.match", Level.WARN);
        CUSTOMER_IS_EXISTS = new StatusCode("0052", "customer.is.exists", Level.WARN);
        FIELD_ERROR = new StatusCode("0053", "field.error", Level.WARN);
        NAME_ALREADY_EXISTS = new StatusCode("0054", "name.already.exists", Level.WARN);
        NOT_CONFIGURED_LINE_BOT_CHANNEL_TOKEN = new StatusCode("0055", "not.configured.line.bot.channel.token", Level.WARN);
        THE_CURRENT_STATUS_CANNOT_BE_ROLLED_BACK = new StatusCode("0056", "the.current.status.cannot.be.rolled.back", Level.WARN);
        STORE_CODE_IS_NOT_EXISTS = new StatusCode("0057", "store.code.is.not.exists", Level.WARN);
        ONLY_SUPPORTED_CLASSIC_OR_ECO = new StatusCode("0058", "only.supported.classic.or.eco", Level.WARN);
        MEMBERTYPE_IS_NOT_EXISTS = new StatusCode("0059", "membertype.is.not.exists", Level.WARN);
        TITLE_IS_NOT_NULL = new StatusCode("0060", "title.is.not.null", Level.WARN);
        SEGMENT_IS_NOT_EXISTS = new StatusCode("0061", "segment.is.not.exists", Level.WARN);
        CHANNELTYPE_ONLY_SUPPORTED_CLASSIC_OR_ECO = new StatusCode("0062", "channeltype.only.supported.classic.or.eco", Level.WARN);
        THE_NUMBER_OF_PROD_STORAGE_IS_MORE_THAN_20000 = new StatusCode("0063", "the.number.of.prod.storage.is.more.than.20000", Level.WARN);
        SAVE_MM_EXCEPTION = new StatusCode("0064", "save.mm.exception", Level.WARN);
        SAVE_BOUNCERATE_EXCEPTION = new StatusCode("0065", "save.bouncerate.exception", Level.WARN);
        SAVE_PRODUCT_EXCEPTION = new StatusCode("0066", "save.product.exception", Level.WARN);
        NO_SUCH_CHANNEL = new StatusCode("0067", "no.such.channel", Level.WARN);
        CUSTOMER_EMAIL_IS_ILLEGAL = new StatusCode("0068", "customer.email.is.illegal", Level.WARN);
        CUSTOMER_LINE_IS_ILLEGAL = new StatusCode("0069", "customer.line.is.illegal", Level.WARN);
        CAN_ONLY_BE_SENT_ONCE_IN_30S = new StatusCode("0070", "can.only.be.sent.once.in.30s", Level.WARN);
        NO_SENDABLE_CUSTOMERS = new StatusCode("0071", "no.sendable.customers", Level.WARN);
        TEMPLATE_CONTENT_CANNOT_EXCEED_300 = new StatusCode("0072", "template.content.cannot.exceed.300", Level.WARN);
    }

    public AdminStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }

    public AdminStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }
}
