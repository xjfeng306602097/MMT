package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdBrand;

import java.util.List;

/**
 *
 */
public interface ProdBrandService extends IService<ProdBrand> {
    List<String> getBrandNames();

    IPage<ProdBrand> list(Page<ProdBrand> page, ProdBrand brand);

    Boolean update(String id, ProdBrand brand);
}
