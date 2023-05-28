package com.makro.mall.product.pojo.dto;

import com.makro.mall.product.pojo.entity.ProdList;
import lombok.Data;

/**
 * @Description: 前端引用了itemcode/urlparam逻辑分开且较多处，所以新增字段itemcode同ProdList的urlparam
 * @Author: Zidan
 * @Date: 2022-04-30
 **/
@Data
public class ProdInfo extends ProdList {
    private String itemcode;
}
