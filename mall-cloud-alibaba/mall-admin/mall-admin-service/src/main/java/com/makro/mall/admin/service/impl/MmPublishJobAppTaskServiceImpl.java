package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.service.CommonPublishJobTaskService;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.admin.service.MmPublishJobAppTaskService;
import com.makro.mall.stat.pojo.api.AppFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MmPublishJobAppTaskServiceImpl implements MmPublishJobAppTaskService, CommonPublishJobTaskService {


    private final AppFeignClient appFeignClient;
    private final MmActivityService mmActivityService;

    @Override
    public Set<Long> getMmPublishTotal(String mmCode) {
        MmActivity activity = mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().select(MmActivity::getStartTime, MmActivity::getEndTime).eq(MmActivity::getMmCode, mmCode));
        if (ObjectUtil.isNull(activity)) {
            return Set.of();
        }
        Date startTime = activity.getStartTime();
        Date endTime = activity.getEndTime();
        if (ObjectUtil.isNull(startTime) || ObjectUtil.isNull(endTime)) {
            return Set.of();
        }
        Set<String> customerIds = appFeignClient.getTotalByTime(startTime, endTime).getData();
        if (CollUtil.isEmpty(customerIds)) {
            return Set.of();
        }
        return customerIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }
}
