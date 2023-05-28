package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.dto.ChannelvisitorsDTO;
import com.makro.mall.stat.pojo.snapshot.ChannelVisitorConversion;
import com.makro.mall.stat.pojo.vo.BarChartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【channel_visitor_conversion】的数据库操作Mapper
 * @createDate 2022-07-05 10:27:07
 * @Entity com.makro.mall.stat.pojo.snapshot.ChannelVisitorConversion
 */
@Mapper
public interface ChannelVisitorConversionMapper extends BaseMapper<ChannelVisitorConversion> {


    void saveChannelVisitorConversion(List<ChannelVisitorConversion> collect, String s);

    List<ChannelvisitorsDTO> listForChannel(@Param("mmCode") String mmCode, @Param("sql") String format2Str);

    List<BarChartVO> list(@Param("mmCode") String mmCode, @Param("sql") String format2Str);

    Boolean hasTimeData(String sql);

    Long sumChannelPie(String mmCode, String sql, String channel);
}




