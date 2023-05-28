package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.AssemblyDataByMemberType;
import com.makro.mall.stat.pojo.vo.BarChartVO;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【assembly_data_by_member_type】的数据库操作Service
 * @createDate 2022-07-11 10:16:42
 */
public interface AssemblyDataByMemberTypeService extends IService<AssemblyDataByMemberType> {

    void saveMemberTypeClickThroughRate(List<AssemblyDataByMemberType> data, Date time);

    List<BarChartVO> list(String mmCode, Date startTime, Date endTime, List<String> memberTypeList);

    Boolean hasTimeData(Date time);

    Long sumCustomerTypePie(String mmCode, Date startTime, Date endTime, String name);
}
