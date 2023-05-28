package com.makro.mall.stat.controller;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.service.AssemblyService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * 指标接口
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/13
 */
@Api(tags = "定时任务数据组装接口", hidden = true)
@RestController
@RequestMapping("/api/v1/assembly")
@RequiredArgsConstructor
@Slf4j
public class AssemblyController {

    private final AssemblyService assemblyService;


    /*********************************************3.14日************************************************************/

    @GetMapping("/mixedPanelSummary")
    public BaseResponse<Boolean> mixedPanelSummary(@RequestParam("time") Date time) {
        assemblyService.saveMixedPanelSummary(time);
        return BaseResponse.success();
    }
    @GetMapping("/pageTotalSuccess")
    public BaseResponse<Boolean> pageTotalSuccess(@RequestParam("time") Date time) {
        assemblyService.pageTotalSuccess(time);
        return BaseResponse.success();
    }


    /*********************************************行为分析************************************************************/
    @GetMapping("/behaviorData")
    public BaseResponse<Boolean> behaviorData(@RequestParam("time") Date time) {
        assemblyService.saveBehaviorData(time);
        return BaseResponse.success();
    }

    @GetMapping("/visitorClicksOnProduct")
    public BaseResponse<Boolean> visitorClicksOnProduct(@RequestParam("time") Date time) {
        assemblyService.saveVisitorClicksOnProduct(time);
        return BaseResponse.success();
    }

    @GetMapping("/averageVisitorVisits")
    public BaseResponse<Boolean> averageVisitorVisits(@RequestParam("time") Date time) {
        assemblyService.averageVisitorVisits(time);
        return BaseResponse.success();
    }

    @GetMapping("/channelVisitorConversion")
    public BaseResponse<Boolean> channelVisitorConversion(@RequestParam("time") Date time) {
        assemblyService.channelVisitorConversion(time);
        return BaseResponse.success();
    }

    @GetMapping("/memberTypeClickThroughRate")
    public BaseResponse<Boolean> memberTypeClickThroughRate(@RequestParam("time") Date time) {
        assemblyService.memberTypeClickThroughRate(time);
        return BaseResponse.success();
    }

    /*********************************************商品分析************************************************************/
    @GetMapping("/productAnalysis")
    public BaseResponse<Boolean> productAnalysis(@RequestParam("time") Date time) {
        assemblyService.productAnalysis(time);
        return BaseResponse.success();
    }

    @GetMapping("/visitsAnalysis")
    public BaseResponse<Boolean> visitsAnalysis(@RequestParam(value = "time", required = false) Date time) {
        assemblyService.visitsAnalysis(time);
        return BaseResponse.success();
    }

    @GetMapping("/visitorsAnalysis")
    public BaseResponse<Boolean> visitorsAnalysis(@RequestParam(value = "time", required = false) Date time) {
        assemblyService.visitorsAnalysis(time);
        return BaseResponse.success();
    }

    /*********************************************模块************************************************************/
    @GetMapping("/mostVisitPage")
    public BaseResponse<Boolean> mostVisitPage(@RequestParam("time") Date time) {
        assemblyService.mostVisitPage(time);
        return BaseResponse.success();
    }

    @GetMapping("/friends")
    public BaseResponse<Boolean> friends(@RequestParam("time") Date time) {
        assemblyService.friends(time);
        return BaseResponse.success();
    }

    @GetMapping("/pageStay")
    public BaseResponse<Boolean> syncPageStay(@RequestParam(value = "time", required = false) Date time) {
        log.info("sync page stay time now, time is {}", time);
        assemblyService.assemblePageStay(time);
        return BaseResponse.success();
    }

    @GetMapping("/stayPageNo")
    public BaseResponse<Boolean> stayPageNo(@RequestParam(value = "time", required = false) Date time) {
        assemblyService.stayPageNo(time);
        return BaseResponse.success();

    }


    @GetMapping("/generation")
    @Transactional
    public BaseResponse<Boolean> generation(@RequestParam(value = "time", required = false) Date time) {
        assemblyService.saveBehaviorData(time);
        assemblyService.saveVisitorClicksOnProduct(time);
        assemblyService.averageVisitorVisits(time);
        assemblyService.channelVisitorConversion(time);
        assemblyService.memberTypeClickThroughRate(time);
        assemblyService.productAnalysis(time);
        assemblyService.mostVisitPage(time);
        assemblyService.friends(time);
        assemblyService.stayPageNo(time);
        assemblyService.saveMixedPanelSummary(time);
        assemblyService.pageTotalSuccess(time);

        for (int i = 0; i < 24; i++) {
            time.setHours(i);
            assemblyService.visitsAnalysis(time);
            assemblyService.visitorsAnalysis(time);
            assemblyService.assemblePageStay(time);
        }

        return BaseResponse.success();

    }

}
