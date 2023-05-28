package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.PageStaySummary;

/**
 *
 */
public interface PageStaySummaryService extends IService<PageStaySummary> {

    boolean hasTargetHourData(String hour);
}
