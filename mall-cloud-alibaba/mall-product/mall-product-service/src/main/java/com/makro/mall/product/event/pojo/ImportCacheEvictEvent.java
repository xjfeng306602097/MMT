package com.makro.mall.product.event.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/4/6
 */
@Data
@AllArgsConstructor
public class ImportCacheEvictEvent {

    private String mmCode;

}
