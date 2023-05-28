package com.makro.mall.stat.service;


import com.makro.mall.stat.pojo.metadata.PageStayLog;
import com.makro.mall.stat.pojo.snapshot.PageStaySummary;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/30 element文件夹
 */
public interface AssemblyService {

    void saveBehaviorData(Date time);

    void saveVisitorClicksOnProduct(Date time);

    void averageVisitorVisits(Date time);

    void channelVisitorConversion(Date time);

    void memberTypeClickThroughRate(Date time);

    void productAnalysis(Date time);

    void visitsAnalysis(Date calTime);

    void visitorsAnalysis(Date calTime);

    void assemblePageStay(Date calTime);

    void mostVisitPage(Date time);

    void friends(Date time);

    void stayPageNo(Date time);

    void saveMixedPanelSummary(Date time);

    void pageTotalSuccess(Date time);

    @NotNull
    Map<String, PageStaySummary> getPageStaySummaryMap(Date calDate, String calHour, List<PageStayLog> stayLogs);
}
