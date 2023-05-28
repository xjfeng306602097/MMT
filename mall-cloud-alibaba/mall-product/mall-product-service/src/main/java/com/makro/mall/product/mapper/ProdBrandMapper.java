package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.product.pojo.entity.ProdBrand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.product.pojo.entity.ProdBrand
 */
@Mapper
public interface ProdBrandMapper extends BaseMapper<ProdBrand> {
    List<String> getBrandNames();
}




