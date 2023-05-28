package com.makro.mall.common.model;

import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/15
 */
@Data
public abstract class BasePageRequest {

    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final Integer DEFAULT_PAGE_NUM = 1;

    private Integer page;

    private Integer limit;

    public BasePageRequest() {
        this.page = DEFAULT_PAGE_NUM;
        this.limit = DEFAULT_PAGE_SIZE;
    }

    public BasePageRequest(final Integer page, final Integer limit) {
        this.page = page;
        this.limit = limit;
    }

}
