package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.AverageVisitorVisits;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【average_visitor_visits】的数据库操作Service
 * @createDate 2022-07-04 15:44:17
 */
public interface AverageVisitorVisitsService extends IService<AverageVisitorVisits> {

    void saveVisitorClicksOnProduct(List<AverageVisitorVisits> list, String channel, Date time);

    List<AverageVisitorVisits> list(String mmCode, Date startTime, Date endTime);

    boolean hasTimeData(Date time);
}
