package com.makro.mall.product.pojo.dto.request;

import com.makro.mall.common.model.BasePageRequest;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/4/24
 */
@Data
public class MakroMailProQueryReq extends BasePageRequest {

    private String q;

    private boolean querySuggestions;
}
