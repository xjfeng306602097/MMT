package com.makro.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdTemplateDetails;

import java.util.List;

/**
 *
 */
public interface ProdTemplateDetailsService extends IService<ProdTemplateDetails> {
    Boolean saveProdTemplateDetails(ProdTemplateDetails templateDetails);

    Boolean saveBatchProdTemplateDetails(List<ProdTemplateDetails> list);
}
