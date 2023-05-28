package com.makro.mall.stat.pojo.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.pojo.api.fallback.AppFeignFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Set;

/**
 * 功能描述: 用户行为分析定时任务
 *
 * @Author: 卢嘉俊
 * @Date: 2022/7/8 用户行为分析
 */
@FeignClient(value = "makro-stat-collector", fallback = AppFeignFallbackClient.class, contextId = "stat-app")
public interface AppFeignClient {


    String API_V1_APP = "/api/v1/app";

    @GetMapping(value = API_V1_APP + "/getTotalByTime")
    BaseResponse<Set<String>> getTotalByTime(@RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                             @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime);
}

