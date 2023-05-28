package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.product.pojo.entity.ProdPic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.makro.mall.admin.pojo.entity.ProdPic
 */
@Mapper
public interface ProdPicMapper extends BaseMapper<ProdPic> {

    @Select("SELECT * FROM PROD_PIC WHERE ITEM_CODE = (SELECT ITEM_CODE FROM PROD_PIC WHERE ID = #{id})")
    List<ProdPic> listRelatedPicById(Long id);

}




