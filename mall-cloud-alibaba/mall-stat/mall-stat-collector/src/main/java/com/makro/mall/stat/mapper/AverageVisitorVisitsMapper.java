package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.AverageVisitorVisits;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【average_visitor_visits】的数据库操作Mapper
 * @createDate 2022-07-04 15:44:17
 * @Entity com.makro.mall.stat.pojo.snapshot.AverageVisitorVisits
 */
@Mapper
public interface AverageVisitorVisitsMapper extends BaseMapper<AverageVisitorVisits> {

    void saveVisitorClicksOnProduct(List<AverageVisitorVisits> list, String channel, String s);

    List<AverageVisitorVisits> list(@Param("mmCode") String mmCode, @Param("sql") String format2Str);

    Boolean hasTimeData(String time);
}




