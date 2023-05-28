package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description admin 00
 * file 01
 * auth 02
 * @date 2022/5/12
 */
public class FileStatusCode extends StatusCode {


    public static final StatusCode WRONG_FOLDER_NAME;

    public static final StatusCode ABOVE_MAX_UPLOAD_SIZE;

    static {
        WRONG_FOLDER_NAME = new StatusCode("0131", "folder.name.is.wrong", Level.WARN);
        ABOVE_MAX_UPLOAD_SIZE = new StatusCode("0132", "over.max.upload.size", Level.WARN);
    }

    public FileStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }

    public FileStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }
}
