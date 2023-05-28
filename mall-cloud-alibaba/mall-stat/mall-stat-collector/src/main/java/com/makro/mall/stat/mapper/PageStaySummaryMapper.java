package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.PageStaySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.makro.mall.stat.pojo.entity.PageStaySummary
 */
@Mapper
public interface PageStaySummaryMapper extends BaseMapper<PageStaySummary> {

    Boolean hasTargetHourData(@Param("hour") String hour);

}




