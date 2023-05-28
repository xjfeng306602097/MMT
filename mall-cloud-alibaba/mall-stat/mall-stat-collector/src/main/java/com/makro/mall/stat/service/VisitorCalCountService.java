package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.VisitorCalCount;

import java.util.Date;
import java.util.List;

/**
 *
 */
public interface VisitorCalCountService extends IService<VisitorCalCount> {

    boolean hasTargetHourData(String hour);

    List<VisitorCalCount> listByCodeAndTime(String mmCode, Date time);
}
