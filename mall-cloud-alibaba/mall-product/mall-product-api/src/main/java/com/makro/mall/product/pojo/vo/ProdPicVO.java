package com.makro.mall.product.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/25
 */
@Data
public class ProdPicVO {

    private Long id;

    private String itemCode;

    private String creator;

    private String lastUpdater;

    private Long defaulted;

    private Long deleted;

    protected LocalDateTime gmtCreate;

    protected LocalDateTime gmtModified;

}
