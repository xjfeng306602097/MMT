package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.VisitsCalCount;

import java.util.Date;
import java.util.List;

/**
 *
 */
public interface VisitsCalCountService extends IService<VisitsCalCount> {

    boolean hasTargetHourData(String hour);

    List<VisitsCalCount> listByCodeAndTime(String mmCode, Date time);
}
