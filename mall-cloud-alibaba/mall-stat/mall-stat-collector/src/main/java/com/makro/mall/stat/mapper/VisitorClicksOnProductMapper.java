package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【visitor_clicks_on_product】的数据库操作Mapper
 * @createDate 2022-07-04 14:11:36
 * @Entity com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct
 */
@Mapper
public interface VisitorClicksOnProductMapper extends BaseMapper<VisitorClicksOnProduct> {

    void saveVisitorClicksOnProduct(@Param("list") List<VisitorClicksOnProduct> list, @Param("member") String member, String time);

    List<VisitorClicksOnProduct> list(@Param("mmCode") String mmCode, @Param("sql") String sql);

    Boolean hasTimeData(String s);
}




