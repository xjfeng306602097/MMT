package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.product.pojo.entity.ProdAux;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.product.pojo.entity.ProdAux
 */
@Mapper
public interface ProdAuxMapper extends BaseMapper<ProdAux> {
    List<String> getAuxType();

    List<ProdAux> list(Page<ProdAux> page, ProdAux aux);

    boolean saveIfNotExist(ProdAux aux);
}




