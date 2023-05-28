package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.BehaviorData;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【behavior_data】的数据库操作Service
 * @createDate 2022-07-04 10:36:16
 */
public interface BehaviorDataService extends IService<BehaviorData> {

    List<BehaviorData> list(String mmCode, Date startTime, Date endTime);

    boolean hasTimeData(Date time);

    List<BehaviorData> mostVisitPage(String mmCode, String start, String end);

    void saveBehaviorData(List<BehaviorData> behaviorData, Date time);
}
