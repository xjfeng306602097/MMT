package com.makro.mall.stat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.PageStaySummaryMapper;
import com.makro.mall.stat.pojo.snapshot.PageStaySummary;
import com.makro.mall.stat.service.PageStaySummaryService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class PageStaySummaryServiceImpl extends ServiceImpl<PageStaySummaryMapper, PageStaySummary>
        implements PageStaySummaryService {

    @Override
    public boolean hasTargetHourData(String hour) {
        return baseMapper.hasTargetHourData(hour);
    }

}




