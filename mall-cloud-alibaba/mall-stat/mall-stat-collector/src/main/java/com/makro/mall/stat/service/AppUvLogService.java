package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.metadata.AppUvLog;

import java.util.Date;
import java.util.Set;

/**
 * @author jincheng
 * @description 针对表【app_uv_log】的数据库操作Service
 * @createDate 2023-03-08 10:24:56
 */
public interface AppUvLogService extends IService<AppUvLog> {

    Set<String> getTotalByTime(Date startTime, Date endTime);

    Long getMmPublishAppUv(Date time);
}
