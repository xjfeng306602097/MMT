package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.VisitsCalCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.makro.mall.stat.pojo.entity.VisitsCalCount
 */
@Mapper
public interface VisitsCalCountMapper extends BaseMapper<VisitsCalCount> {

    Boolean hasTargetHourData(@Param("hour") String hour);

    List<VisitsCalCount> listByCodeAndTime(@Param("mmCode") String mmCode, @Param("sql") String sql);
}




