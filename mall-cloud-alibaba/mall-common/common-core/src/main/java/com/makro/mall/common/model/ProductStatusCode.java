package com.makro.mall.common.model;

import org.slf4j.event.Level;

/**
 * @author xiaojunfeng
 * @description admin 00
 * file 01
 * auth 02
 * message 03
 * product 04
 * @date 2022/5/12
 */
public class ProductStatusCode extends StatusCode {


    public static final StatusCode BRANDID_IS_EMPTY;
    public static final StatusCode ICONID_IS_EMPTY;
    public static final StatusCode ICON_PICTURE_DEFAULTED_REQUIRED;
    public static final StatusCode THE_NUMBER_OF_PROD_STORAGE_IS_MORE_THAN_20000;
    public static final StatusCode ITEM_CODE_IS_EMPTY;
    public static final StatusCode ASSOCIATED_PRODUCTS_CANNOT_BE_DELETED;
    public static final StatusCode ITEM_CODE_CANNOT_BE_DUPLICATED;
    public static final StatusCode THE_PAGE_AND_SORT_OF_THE_CURRENT_PRODUCT_ALREADY_EXIST;

    static {
        BRANDID_IS_EMPTY = new StatusCode("0401", "brandId.is.empty", Level.WARN);
        ICONID_IS_EMPTY = new StatusCode("0402", "iconId.is.empty", Level.WARN);
        ICON_PICTURE_DEFAULTED_REQUIRED = new StatusCode("0403", "icon.picture.defaulted.required", Level.WARN);
        THE_NUMBER_OF_PROD_STORAGE_IS_MORE_THAN_20000 = new StatusCode("0404", "the.number.of.prod.storage.is.more.than.20000", Level.WARN);
        ITEM_CODE_IS_EMPTY = new StatusCode("0405", "item.code.is.empty", Level.WARN);
        ASSOCIATED_PRODUCTS_CANNOT_BE_DELETED = new StatusCode("0406", "associated.products.cannot.be.deleted", Level.WARN);
        ITEM_CODE_CANNOT_BE_DUPLICATED = new StatusCode("0407", "item.code.cannot.be.duplicated", Level.WARN);
        THE_PAGE_AND_SORT_OF_THE_CURRENT_PRODUCT_ALREADY_EXIST = new StatusCode("0408", "the.page.and.sort.of.the.current.product.already.exist", Level.WARN);
    }

    public ProductStatusCode(String code, String msg, Level level) {
        super(code, msg, level);
    }

    public ProductStatusCode(String code, String msg, Level level, Object... args) {
        super(code, msg, level, args);
    }

}
