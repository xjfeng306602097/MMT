package com.makro.mall.stat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.VisitsCalCountMapper;
import com.makro.mall.stat.pojo.snapshot.VisitsCalCount;
import com.makro.mall.stat.service.VisitsCalCountService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 */
@Service
public class VisitsCalCountServiceImpl extends ServiceImpl<VisitsCalCountMapper, VisitsCalCount>
        implements VisitsCalCountService {

    @Override
    public boolean hasTargetHourData(String hour) {
        return baseMapper.hasTargetHourData(hour);
    }

    @Override
    public List<VisitsCalCount> listByCodeAndTime(String mmCode, Date time) {
        return baseMapper.listByCodeAndTime(mmCode, ClickHouseDateUtil.formatEqualsStr(time));
    }
}




