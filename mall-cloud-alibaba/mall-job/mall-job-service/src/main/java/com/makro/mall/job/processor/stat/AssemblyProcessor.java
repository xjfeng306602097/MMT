package com.makro.mall.job.processor.stat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.stat.pojo.api.AssemblyFeignClient;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/15 MmSegment过期失效定时调度
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AssemblyProcessor {

    private final AssemblyFeignClient assemblyFeignClient;

    @XxlJob("pageTotalSuccess")
    public Boolean pageTotalSuccess() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.pageTotalSuccess(date).getData();
    }

    @XxlJob("mixedPanelSummary")
    public Boolean mixedPanelSummary() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.mixedPanelSummary(date).getData();
    }

    @XxlJob("averageVisitorVisitsProcessor")
    public Boolean averageVisitorVisitsProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.averageVisitorVisits(date).getData();
    }

    @XxlJob("behaviorDataProcessor")
    public Boolean behaviorDataProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.behaviorData(date).getData();
    }

    @XxlJob("channelVisitorConversion")
    public Boolean channelVisitorConversionProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.channelVisitorConversion(date).getData();
    }

    @XxlJob("memberTypeClickThroughRate")
    public Boolean memberTypeClickThroughRateProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.memberTypeClickThroughRate(date).getData();
    }

    @XxlJob("friends")
    public Boolean pageChannelMemberTypeProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.friends(date).getData();
    }

    @XxlJob("mostVisitPage")
    public Boolean pagePageNoProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.mostVisitPage(date).getData();
    }

    @XxlJob("syncPageStay")
    public Boolean pageStaySummaryProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDateTime(XxlJobHelper.getJobParam()) : new Date();
        log.info("sync page stay time now, time is {}", date);
        return assemblyFeignClient.syncPageStay(date).getData();
    }

    @XxlJob("stayPageNo")
    public Boolean stayPageNoProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.stayPageNo(date).getData();
    }

    @XxlJob("visitorClicksOnProduct")
    public Boolean visitorClicksOnProductProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.visitorClicksOnProduct(date).getData();
    }

    @XxlJob("visitorsAnalysis")
    public Boolean visitorsAnalysisProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDateTime(XxlJobHelper.getJobParam()) : new Date();
        log.info("visitorsAnalysis xxl date:{}", date);
        return assemblyFeignClient.visitorsAnalysis(date).getData();
    }

    @XxlJob("visitsAnalysis")
    public Boolean visitsAnalysisProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDateTime(XxlJobHelper.getJobParam()) : new Date();
        return assemblyFeignClient.visitsAnalysis(date).getData();
    }

    @XxlJob("productAnalysis")
    public Boolean productAnalysisProcessor() {
        Date date = StrUtil.isNotEmpty(XxlJobHelper.getJobParam()) ? DateUtil.parseDate(XxlJobHelper.getJobParam()) : DateUtil.yesterday();
        return assemblyFeignClient.productAnalysis(date).getData();
    }

}
