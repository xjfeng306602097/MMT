package com.makro.mall.admin.pojo.vo;


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

    public TreeSelectVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Long id;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TreeSelectVO> children;

}
