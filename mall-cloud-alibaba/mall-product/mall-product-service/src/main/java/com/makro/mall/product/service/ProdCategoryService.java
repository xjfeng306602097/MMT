package com.makro.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ProdCategory;
import com.makro.mall.product.pojo.vo.CategoryVO;
import com.makro.mall.product.pojo.vo.TreeSelectVO;

import java.util.List;

/**
 *
 */
public interface ProdCategoryService extends IService<ProdCategory> {

    List<CategoryVO> listTable(Integer status, String name, Boolean hasChild);

    List<TreeSelectVO> listTreeSelect();

    boolean saveCategory(ProdCategory category);

    boolean deleteByIds(String ids);
}
