package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.BehaviorDataMapper;
import com.makro.mall.stat.pojo.snapshot.BehaviorData;
import com.makro.mall.stat.service.BehaviorDataService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【behavior_data】的数据库操作Service实现
 * @createDate 2022-07-04 10:36:16
 */
@Service
public class BehaviorDataServiceImpl extends ServiceImpl<BehaviorDataMapper, BehaviorData>
        implements BehaviorDataService {

    @Override
    public List<BehaviorData> list(String mmCode, Date startTime, Date endTime) {
        return getBaseMapper().list(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.formatEqualsStr(time)), true);
    }

    @Override
    public List<BehaviorData> mostVisitPage(String mmCode, String start, String end) {
        return getBaseMapper().mostVisitPage(mmCode, start, end);
    }

    @Override
    public void saveBehaviorData(List<BehaviorData> behaviorData, Date time) {
        getBaseMapper().saveBehaviorData(behaviorData, ClickHouseDateUtil.format2Str(time));
    }
}




