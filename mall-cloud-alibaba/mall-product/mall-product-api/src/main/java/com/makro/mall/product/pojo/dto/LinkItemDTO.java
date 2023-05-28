package com.makro.mall.product.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkItemDTO {
    private String parentCode;
    private String parentId;
    private Set<String> linkItemCodes;
}
