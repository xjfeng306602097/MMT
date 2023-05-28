package com.makro.mall.stat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.VisitorCalCountMapper;
import com.makro.mall.stat.pojo.snapshot.VisitorCalCount;
import com.makro.mall.stat.service.VisitorCalCountService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
public class VisitorCalCountServiceImpl extends ServiceImpl<VisitorCalCountMapper, VisitorCalCount>
        implements VisitorCalCountService {

    @Override
    public boolean hasTargetHourData(String hour) {
        return baseMapper.hasTargetHourData(hour);
    }

    @Override
    public List<VisitorCalCount> listByCodeAndTime(String mmCode, Date time) {
        return baseMapper.listByCodeAndTime(mmCode, ClickHouseDateUtil.formatEqualsStr(time));
    }

}




