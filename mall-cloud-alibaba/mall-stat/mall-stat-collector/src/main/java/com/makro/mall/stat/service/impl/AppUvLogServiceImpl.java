package com.makro.mall.stat.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.AppUvLogMapper;
import com.makro.mall.stat.pojo.metadata.AppUvLog;
import com.makro.mall.stat.service.AppUvLogService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jincheng
 * @description 针对表【app_uv_log】的数据库操作Service实现
 * @createDate 2023-03-08 10:24:56
 */
@Service
public class AppUvLogServiceImpl extends ServiceImpl<AppUvLogMapper, AppUvLog>
        implements AppUvLogService {

    @Override
    public Set<String> getTotalByTime(Date startTime, Date endTime) {
        List<AppUvLog> list = list(new LambdaQueryWrapper<AppUvLog>()
                .between(AppUvLog::getEventDate, DateUtil.formatDate(startTime), DateUtil.formatDate(endTime))
                .ne(AppUvLog::getMemberNo, "")
                .isNotNull(AppUvLog::getMemberNo));
        return list.stream().map(AppUvLog::getMemberNo).collect(Collectors.toSet());
    }

    @Override
    public Long getMmPublishAppUv(Date time) {
        int size = list(new LambdaQueryWrapper<AppUvLog>()
                .select(AppUvLog::getUuid)
                .eq(AppUvLog::getEventDate, DateUtil.formatDate(time))
                .groupBy(AppUvLog::getUuid)).size();
        return (long) size;
    }

}




