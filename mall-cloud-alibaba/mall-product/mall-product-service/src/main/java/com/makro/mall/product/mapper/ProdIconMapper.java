package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.product.pojo.entity.ProdIcon;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.product.pojo.entity.ProdIcon
 */
@Mapper
public interface ProdIconMapper extends BaseMapper<ProdIcon> {
    List<String> getIconNames();
}




