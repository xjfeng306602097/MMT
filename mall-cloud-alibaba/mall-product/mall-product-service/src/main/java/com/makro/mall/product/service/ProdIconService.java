package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdIcon;
import com.makro.mall.product.pojo.entity.ProdIconPic;

import java.util.List;

/**
 *
 */
public interface ProdIconService extends IService<ProdIcon> {
    List<String> getIconNames();

    IPage<ProdIcon> list(Page<ProdIcon> page, ProdIcon icon);

    Boolean update(String id, ProdIcon icon);

    Boolean updatePic(String id, ProdIconPic pic);
}
