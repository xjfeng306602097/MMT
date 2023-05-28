package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.BehaviorData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【behavior_data】的数据库操作Mapper
 * @createDate 2022-07-04 10:36:16
 * @Entity com.makro.mall.stat.pojo.snapshot.BehaviorData
 */
@Mapper
public interface BehaviorDataMapper extends BaseMapper<BehaviorData> {

    List<BehaviorData> list(@Param("mmCode") String mmCode, @Param("sql") String format2Str);

    Boolean hasTimeData(String sql);

    List<BehaviorData> mostVisitPage(String mmCode, String start, String end);

    void saveBehaviorData(List<BehaviorData> list, String time);
}




