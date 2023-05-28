package com.makro.mall.stat.pojo.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.pojo.api.fallback.AssemblyFeignFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 功能描述: 用户行为分析定时任务
 *
 * @Author: 卢嘉俊
 * @Date: 2022/7/8 用户行为分析
 */
@FeignClient(value = "makro-stat-collector", fallback = AssemblyFeignFallbackClient.class, contextId = "mm-activity-ex-client")
public interface AssemblyFeignClient {


    String ASSEMBLY = "/api/v1/assembly";

    @GetMapping(value = ASSEMBLY + "/behaviorData")
    BaseResponse<Boolean> behaviorData(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/visitorClicksOnProduct")
    BaseResponse<Boolean> visitorClicksOnProduct(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/averageVisitorVisits")
    BaseResponse<Boolean> averageVisitorVisits(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/channelVisitorConversion")
    BaseResponse<Boolean> channelVisitorConversion(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/productAnalysis")
    BaseResponse<Boolean> productAnalysis(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/memberTypeClickThroughRate")
    BaseResponse<Boolean> memberTypeClickThroughRate(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/visitsAnalysis")
    BaseResponse<Boolean> visitsAnalysis(@RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/visitorsAnalysis")
    BaseResponse<Boolean> visitorsAnalysis(@RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/mostVisitPage")
    BaseResponse<Boolean> mostVisitPage(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(value = ASSEMBLY + "/friends")
    BaseResponse<Boolean> friends(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(ASSEMBLY + "/pageStay")
    BaseResponse<Boolean> syncPageStay(@RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(ASSEMBLY + "/stayPageNo")
    BaseResponse<Boolean> stayPageNo(@RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(ASSEMBLY + "/mixedPanelSummary")
    BaseResponse<Boolean> mixedPanelSummary(@RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);

    @GetMapping(ASSEMBLY + "/pageTotalSuccess")
    BaseResponse<Boolean> pageTotalSuccess(@RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);
}
