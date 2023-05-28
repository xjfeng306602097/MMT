package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.VisitorCalCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.makro.mall.stat.pojo.entity.VisitorCalCount
 */
@Mapper
public interface VisitorCalCountMapper extends BaseMapper<VisitorCalCount> {

    Boolean hasTargetHourData(@Param("hour") String hour);

    List<VisitorCalCount> listByCodeAndTime(@Param("mmCode") String mmCode, @Param("sql") String sql);
}




