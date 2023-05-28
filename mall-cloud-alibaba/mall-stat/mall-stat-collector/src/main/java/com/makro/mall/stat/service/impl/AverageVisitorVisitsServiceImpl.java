package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.AverageVisitorVisitsMapper;
import com.makro.mall.stat.pojo.snapshot.AverageVisitorVisits;
import com.makro.mall.stat.service.AverageVisitorVisitsService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【average_visitor_visits】的数据库操作Service实现
 * @createDate 2022-07-04 15:44:17
 */
@Service
public class AverageVisitorVisitsServiceImpl extends ServiceImpl<AverageVisitorVisitsMapper, AverageVisitorVisits>
        implements AverageVisitorVisitsService {

    @Override
    public void saveVisitorClicksOnProduct(List<AverageVisitorVisits> list, String channel, Date time) {
        getBaseMapper().saveVisitorClicksOnProduct(list, channel, ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<AverageVisitorVisits> list(String mmCode, Date startTime, Date endTime) {
        return getBaseMapper().list(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.format2Str(time)), true);
    }
}




