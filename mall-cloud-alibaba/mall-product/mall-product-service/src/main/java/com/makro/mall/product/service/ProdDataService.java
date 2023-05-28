package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdData;

import java.util.List;

/**
 *
 */
public interface ProdDataService extends IService<ProdData> {

    IPage<ProdData> list(Page<ProdData> page, ProdData data);
}
