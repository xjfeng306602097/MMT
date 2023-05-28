package com.makro.mall.product.pojo.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 树形下拉视图对象
 */
@Data
@NoArgsConstructor
public class TreeSelectVO {

    public TreeSelectVO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TreeSelectVO> children;

}
