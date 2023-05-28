package com.makro.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdPic;

import java.util.List;

/**
 *
 */
public interface ProdPicService extends IService<ProdPic> {

    List<ProdPic> listRelatedPicById(Long id);

    Boolean add(ProdPic pic);
}
