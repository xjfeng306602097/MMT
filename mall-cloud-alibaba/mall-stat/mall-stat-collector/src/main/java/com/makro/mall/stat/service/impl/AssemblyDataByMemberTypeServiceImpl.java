package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.AssemblyDataByMemberTypeMapper;
import com.makro.mall.stat.pojo.snapshot.AssemblyDataByMemberType;
import com.makro.mall.stat.pojo.vo.BarChartVO;
import com.makro.mall.stat.service.AssemblyDataByMemberTypeService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【assembly_data_by_member_type】的数据库操作Service实现
 * @createDate 2022-07-11 10:16:42
 */
@Service
public class AssemblyDataByMemberTypeServiceImpl extends ServiceImpl<AssemblyDataByMemberTypeMapper, AssemblyDataByMemberType>
        implements AssemblyDataByMemberTypeService {

    @Override
    public void saveMemberTypeClickThroughRate(List<AssemblyDataByMemberType> data, Date time) {
        getBaseMapper().saveMemberTypeClickThroughRate(data, ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<BarChartVO> list(String mmCode, Date startTime, Date endTime, List<String> memberTypeList) {
        return getBaseMapper().list(mmCode, memberTypeList, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public Boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.format2Str(time)), true);
    }

    @Override
    public Long sumCustomerTypePie(String mmCode, Date startTime, Date endTime, String name) {
        return getBaseMapper().sumCustomerTypePie(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime), name);
    }
}




