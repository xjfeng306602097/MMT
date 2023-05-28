package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【product_analysis】的数据库操作Mapper
 * @createDate 2022-07-06 17:06:37
 * @Entity com.makro.mall.stat.pojo.snapshot.ProductAnalysis
 */
@Mapper
public interface ProductAnalysisMapper extends BaseMapper<ProductAnalysis> {

    List<ProductAnalysis> page(@Param("result") MakroPage<ProductAnalysis> result, @Param("mmCode") String mmCode
            , @Param("goodsCode") String itemCode, @Param("nameEn") String nameEn
            , @Param("nameThai") String nameThai, @Param("sql") String format2Str);

    List<ProductAnalysis> listForName(@Param("mmCode") String mmCode, @Param("goodsCodes") List<String> itemCode, @Param("sql") String format2Str);

    List<ProductAnalysis> compareProductList(@Param("mmCode") String mmCode, @Param("goodsCode") String goodsCode, @Param("sql") String format2Str);


    Boolean hasTimeData(String s);

    List<ProductAnalysis> list(@Param("mmCode") String mmCode
            , @Param("goodsCodes") List<String> goodsCodes, @Param("nameEn") String nameEn
            , @Param("nameThai") String nameThai, @Param("sql") String format2Str);
}




