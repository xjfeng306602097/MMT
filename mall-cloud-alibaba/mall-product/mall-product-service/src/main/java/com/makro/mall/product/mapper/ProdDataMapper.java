package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.product.pojo.entity.ProdData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.product.pojo.entity.ProdData
 */
@Mapper
public interface ProdDataMapper extends BaseMapper<ProdData> {

    List<ProdData> list(Page<ProdData> page, ProdData data);
}




