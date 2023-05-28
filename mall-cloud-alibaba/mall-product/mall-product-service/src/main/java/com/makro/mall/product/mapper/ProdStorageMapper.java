package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.product.pojo.entity.ProdStorage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.product.pojo.entity.ProdStorage
 */
@Mapper
public interface ProdStorageMapper extends BaseMapper<ProdStorage> {
    List<ProdStorage> list(Page<ProdStorage> page, ProdStorage storage, String segmentId);
}




