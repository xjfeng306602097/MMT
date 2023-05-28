package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.product.pojo.entity.ProdList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Zidan
 * @description 针对表【PROD_LIST(添加或导入模板里商品设计相关数据，MM商品明细的来源)】的数据库操作Mapper
 * @createDate 2022-04-14 13:08:41
 * @Entity com.makro.mall.product.pojo.entity.ProdList
 */
@Mapper
public interface ProdListMapper extends BaseMapper<ProdList> {
    List<ProdList> list(Page<ProdList> page, ProdList data);

    List<ProdList> linkItemList(String itemCode, String mmCode, Integer isvalid);

    List<String> getPages(String mmCode);

    void updateAll(ProdList data);
}




