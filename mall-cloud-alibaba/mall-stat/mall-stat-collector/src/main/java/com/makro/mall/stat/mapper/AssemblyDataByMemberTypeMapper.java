package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.AssemblyDataByMemberType;
import com.makro.mall.stat.pojo.vo.BarChartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【assembly_data_by_member_type】的数据库操作Mapper
 * @createDate 2022-07-11 10:16:42
 * @Entity com.makro.mall.stat.pojo.snapshot.AssemblyDataByMemberType
 */
@Mapper
public interface AssemblyDataByMemberTypeMapper extends BaseMapper<AssemblyDataByMemberType> {

    void saveMemberTypeClickThroughRate(List<AssemblyDataByMemberType> list, String s);

    List<BarChartVO> list(@Param("mmCode") String mmCode, @Param("list") List<String> memberTypeList, @Param("sql") String format2Str);

    Boolean hasTimeData(String s);

    Long sumCustomerTypePie(String mmCode, String sql, String name);
}




