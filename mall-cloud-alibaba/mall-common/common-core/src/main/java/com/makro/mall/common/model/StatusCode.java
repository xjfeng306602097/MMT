package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description 状态枚举类
 * @date 2021/10/9
 */
public class StatusCode {

    public static final StatusCode SUCCESS;
    public static final StatusCode UNKNOWN_EXCEPTION;
    public static final StatusCode REQUEST_PARAM_ILLEGAL;
    public static final StatusCode REQUEST_SIGNED_INVALID;
    public static final StatusCode REQUEST_DECRYPT_INVALID;
    public static final StatusCode OBJECT_NOT_EXIST;
    public static final StatusCode SERVICE_NOT_AVAILABLE;
    public static final StatusCode ILLEGAL_STATE;
    public static final StatusCode NOT_FOUND;
    public static final StatusCode PARAM_IS_NULL;
    public static final StatusCode SERVICE_DEGRADE;
    public static final StatusCode BUSINESS_EXCEPTION;
    public static final StatusCode USER_NOT_EXIST;
    public static final StatusCode USER_LOGIN_ERROR;
    public static final StatusCode USER_ACCOUNT_LOCKED;
    public static final StatusCode USER_ACCOUNT_INVALID;
    public static final StatusCode USER_EMAIL_EMPTY;
    public static final StatusCode USER_NAME_DUPLICATE;
    public static final StatusCode USERNAME_OR_PASSWORD_ERROR;
    public static final StatusCode PASSWORD_ENTER_EXCEED_LIMIT;
    public static final StatusCode PASSWORD_RESET_URL_EXPIRE;
    public static final StatusCode CLIENT_AUTHENTICATION_FAILED;
    public static final StatusCode TOKEN_INVALID_OR_EXPIRED;
    public static final StatusCode TOKEN_ACCESS_FORBIDDEN;
    public static final StatusCode AUTHORIZED_ERROR;
    public static final StatusCode ACCESS_UNAUTHORIZED;
    public static final StatusCode CAPTCHA_ERROR;
    public static final StatusCode CAPTCHA_TIME_OUT;
    public static final StatusCode CAPTCHA_NOT_EMPTY;
    public static final StatusCode ROLE_CAN_NOT_BE_DELETED;
    public static final StatusCode DICT_ITEMS_EXISTS;
    public static final StatusCode DICT_CODE_DUPLICATE;
    public static final StatusCode FONT_NAME_DUPLICATE;

    public static final StatusCode IMPORT_EXCEL_ERROR;
    public static final StatusCode EXCEL_FORMAT_ERROR;
    public static final StatusCode FILE_ERROR;
    public static final StatusCode FILE_SIZE_LIMIT;

    public static final StatusCode TEMPLATE_IS_LOCKED;
    public static final StatusCode TEMPLATE_NOT_EXISTS;
    public static final StatusCode MM_TEMPLATE_ONLY_EXISTS_ONE;
    public static final StatusCode MM_FLOW_ONLY_EXISTS_ONE;
    public static final StatusCode MM_FLOW_FINISHED;
    public static final StatusCode MM_FLOW_FORBIDDEN;
    public static final StatusCode MM_FLOW_NOT_EXISTS;
    public static final StatusCode MM_FLOW_STEP_DUPLICATE;
    public static final StatusCode MM_FLOW_IS_PROCESSING;
    public static final StatusCode MM_FLOW_IS_PROCESSED;
    public static final StatusCode THE_TEMPLATE_DOES_NOT_ALLOW_ROLLBACK;

    public static final StatusCode TOUSER_ADDRESS_FORMAT_ERROR;
    public static final StatusCode TOUSER_NOT_NULL;
    public static final StatusCode SUBJECT_NOT_NULL;
    public static final StatusCode EMAIL_NOT_EXISTS;

    public static final StatusCode MM_DATA_AUTH_FAILED;

    public static final StatusCode MAX_UPLOAD_COUNT;
    public static final StatusCode WRONG_FILE_NAME;
    public static final StatusCode MMCODE_IS_NOT_NULL;
    public static final StatusCode DRIVER_CLASS_NAME_NOT_FOUND;

    public static final StatusCode CHANNEL_NAME_DUPLICATE;
    public static final StatusCode CHECK_SIGNATURE_ERROR;

    static {
        SUCCESS = new StatusCode("0000", "request.success", Level.INFO);
        UNKNOWN_EXCEPTION = new StatusCode("9999", "unkown.system.error", Level.ERROR);
        REQUEST_PARAM_ILLEGAL = new StatusCode("0001", "request.params.invalid", Level.WARN);
        REQUEST_SIGNED_INVALID = new StatusCode("0002", "request.signature.error", Level.WARN);
        REQUEST_DECRYPT_INVALID = new StatusCode("0003", "request.data.decrypt.error", Level.WARN);
        OBJECT_NOT_EXIST = new StatusCode("0004", "obj.not.exists", Level.WARN);
        SERVICE_NOT_AVAILABLE = new StatusCode("0005", "service.not.avaiable", Level.ERROR);
        ILLEGAL_STATE = new StatusCode("0006", "illegal.state", Level.WARN);
        NOT_FOUND = new StatusCode("404", "not.found", Level.ERROR);
        PARAM_IS_NULL = new StatusCode("400", "request.params.is.null", Level.ERROR);
        SERVICE_DEGRADE = new StatusCode("8888", "feign.service.degrade", Level.ERROR);
        BUSINESS_EXCEPTION = new StatusCode("6666", "business.exception", Level.ERROR);
        USER_LOGIN_ERROR = new StatusCode("0010", "user.login.error", Level.WARN);
        USER_NOT_EXIST = new StatusCode("0011", "user.not.exist", Level.WARN);
        USER_ACCOUNT_LOCKED = new StatusCode("0012", "user.account.locked", Level.WARN);
        USER_ACCOUNT_INVALID = new StatusCode("0013", "user.account.invalid", Level.WARN);
        USERNAME_OR_PASSWORD_ERROR = new StatusCode("0014", "username.or.password.error", Level.WARN);
        PASSWORD_ENTER_EXCEED_LIMIT = new StatusCode("0015", "password.enter.exceed.limit", Level.WARN);
        CLIENT_AUTHENTICATION_FAILED = new StatusCode("0016", "client.authentication.failed", Level.WARN);
        TOKEN_INVALID_OR_EXPIRED = new StatusCode("0017", "token.invalid.or.expired", Level.WARN);
        TOKEN_ACCESS_FORBIDDEN = new StatusCode("0018", "token.access.forbidden", Level.WARN);
        AUTHORIZED_ERROR = new StatusCode("0019", "authorized.error", Level.WARN);
        ACCESS_UNAUTHORIZED = new StatusCode("0020", "access.unauthorized", Level.WARN);
        CAPTCHA_ERROR = new StatusCode("0021", "captcha.error", Level.WARN);
        CAPTCHA_TIME_OUT = new StatusCode("0022", "captcha.time.out", Level.WARN);
        CAPTCHA_NOT_EMPTY = new StatusCode("0023", "captcha.not.empty", Level.WARN);
        ROLE_CAN_NOT_BE_DELETED = new StatusCode("0024", "role.can.not.be.delete", Level.WARN);
        TOUSER_ADDRESS_FORMAT_ERROR = new StatusCode("0025", "toUser.address.format.error", Level.WARN);
        TOUSER_NOT_NULL = new StatusCode("0026", "toUser.not.null", Level.WARN);
        SUBJECT_NOT_NULL = new StatusCode("0027", "subject.not.null", Level.WARN);
        EMAIL_NOT_EXISTS = new StatusCode("0028", "email.not.null", Level.WARN);
        DICT_ITEMS_EXISTS = new StatusCode("0029", "dict.items.exists", Level.WARN);
        DICT_CODE_DUPLICATE = new StatusCode("0030", "dict.code.duplicate", Level.WARN);
        PASSWORD_RESET_URL_EXPIRE = new StatusCode("0031", "password.reset.url.expire", Level.WARN);
        USER_EMAIL_EMPTY = new StatusCode("0032", "user.account.email.empty", Level.WARN);
        USER_NAME_DUPLICATE = new StatusCode("0033", "user.account.name.duplicate", Level.WARN);
        FONT_NAME_DUPLICATE = new StatusCode("0036", "font.name.duplicate", Level.WARN);
        IMPORT_EXCEL_ERROR = new StatusCode("0301", "import.excel.error", Level.ERROR);
        EXCEL_FORMAT_ERROR = new StatusCode("0302", "excel.format.error", Level.ERROR);
        FILE_ERROR = new StatusCode("0303", "file.error", Level.ERROR);
        FILE_SIZE_LIMIT = new StatusCode("0304", "file.size.limit", Level.ERROR);
        TEMPLATE_IS_LOCKED = new StatusCode("2403", "template.is.locked", Level.WARN);
        TEMPLATE_NOT_EXISTS = new StatusCode("2404", "template.not.exists", Level.WARN);
        MM_TEMPLATE_ONLY_EXISTS_ONE = new StatusCode("2405", "mm.template.exists.only.one", Level.WARN);
        MM_FLOW_ONLY_EXISTS_ONE = new StatusCode("2406", "mm.flow.exists.only.one", Level.WARN);
        MM_FLOW_FINISHED = new StatusCode("2407", "mm.flow.finished", Level.WARN);
        MM_FLOW_FORBIDDEN = new StatusCode("2408", "mm.flow.forbidden", Level.WARN);
        MM_DATA_AUTH_FAILED = new StatusCode("2409", "mm.data.auth.failed", Level.WARN);
        MM_FLOW_NOT_EXISTS = new StatusCode("2410", "mm.flow.not.exists", Level.WARN);
        MM_FLOW_STEP_DUPLICATE = new StatusCode("2411", "mm.flow.step.duplicate", Level.WARN);
        MM_FLOW_IS_PROCESSING = new StatusCode("2412", "mm.flow.is.processing", Level.WARN);
        MAX_UPLOAD_COUNT = new StatusCode("0034", "max.upload.count", Level.WARN);
        WRONG_FILE_NAME = new StatusCode("0035", "wrong.file.name", Level.WARN);
        MMCODE_IS_NOT_NULL = new StatusCode("0036", "mmcode.is.not.null", Level.WARN);
        DRIVER_CLASS_NAME_NOT_FOUND = new StatusCode("0037", "driver.class.name.not.found", Level.WARN);
        CHANNEL_NAME_DUPLICATE = new StatusCode("0038", "channel.name.duplicate", Level.WARN);
        MM_FLOW_IS_PROCESSED = new StatusCode("0039", "mm.flow.is.processed", Level.WARN);
        THE_TEMPLATE_DOES_NOT_ALLOW_ROLLBACK = new StatusCode("0040", "the.template.does.not.allow.rollback", Level.WARN);
        CHECK_SIGNATURE_ERROR = new StatusCode("0039", "check.signature.error", Level.ERROR);
    }

    private final String code;
    private final String msg;
    private final Level level;
    private final Object[] args;

    public StatusCode(String code, String msg, Level level) {
        this.code = code;
        this.msg = msg;
        this.level = level;
        args = null;
    }

    public StatusCode(String code, String msg, Level level, Object... args) {
        this.code = code;
        this.msg = msg;
        this.level = level;
        this.args = args;
    }

    public StatusCode code(String code) {
        return new StatusCode(code, this.msg, this.level);
    }

    public StatusCode message(String message) {
        return new StatusCode(this.code, message, this.level);
    }

    public StatusCode args(Object... args) {
        return new StatusCode(this.code, this.msg, this.level, args);
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public Level getLevel() {
        return this.level;
    }

    public Object[] getArgs() {
        return args;
    }

    public boolean containsArgs() {
        return (this.args != null) && (this.args.length > 0);
    }

}
