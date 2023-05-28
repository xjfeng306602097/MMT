package com.makro.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;

/**
 *
 */
public interface ProdTemplateInfoService extends IService<ProdTemplateInfo> {
    Boolean saveProdTemplateInfo(ProdTemplateInfo templateInfo);
}
