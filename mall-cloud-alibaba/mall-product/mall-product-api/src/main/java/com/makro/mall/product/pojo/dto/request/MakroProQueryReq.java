package com.makro.mall.product.pojo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/3/24
 */
@Data
@NoArgsConstructor
public class MakroProQueryReq {

    private String q;

    private Integer page;

    private Integer size;

    private boolean querySuggestions = false;

    public MakroProQueryReq(MakroMailProQueryReq req) {
        this.q = req.getQ();
        this.page = req.getPage();
        this.size = req.getLimit();
        this.querySuggestions = req.isQuerySuggestions();
    }
}
