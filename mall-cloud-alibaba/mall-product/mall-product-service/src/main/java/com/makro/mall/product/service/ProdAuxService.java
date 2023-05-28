package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdAux;

import java.util.List;

/**
 *
 */
public interface ProdAuxService extends IService<ProdAux> {
    List<String> getAuxType();

    IPage<ProdAux> list(Page<ProdAux> page, ProdAux aux);

    boolean saveIfNotExist(ProdAux aux);
}
