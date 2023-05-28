package com.makro.mall.stat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.cache.*;
import com.makro.mall.admin.api.MmBounceRateFeignClient;
import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.admin.pojo.entity.MmBounceRate;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatStatusCode;
import com.makro.mall.stat.manager.AssemblyManager;
import com.makro.mall.stat.pojo.constant.CustomerTypeEnum;
import com.makro.mall.stat.pojo.dto.AverageVisitorVisitsPvMvDTO;
import com.makro.mall.stat.pojo.dto.VisitSummaryDTO;
import com.makro.mall.stat.pojo.metadata.GoodsClickLog;
import com.makro.mall.stat.pojo.metadata.PageStayLog;
import com.makro.mall.stat.pojo.metadata.PageViewLog;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.service.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Calendar.HOUR_OF_DAY;

/**
 * 功能描述:  用于定时任务数据组装
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/21 用户行为分析
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssemblyServiceImpl implements AssemblyService {

    private final GoodsClickLogService goodsClickLogService;
    private final PageViewLogService pageViewLogService;
    private final BehaviorDataService behaviorDataService;
    private final VisitorClicksOnProductService visitorClicksOnProductService;
    private final AverageVisitorVisitsService averageVisitorVisitsService;
    private final ChannelVisitorConversionService channelVisitorConversionService;
    private final ProductAnalysisService productAnalysisService;
    private final AssemblyDataByMemberTypeService assemblyDataByMemberTypeService;
    private final VisitsCalCountService visitsCalCountService;
    private final VisitorCalCountService visitorCalCountService;
    private final PagePageNoService pagePageNoService;
    private final PageChannelMemberTypeService pageChannelMemberTypeService;
    private final PageStayLogService pageStayLogService;
    private final PageStaySummaryService pageStaySummaryService;
    private final StayPageNoService stayPageNoService;
    private final AppUvLogService appUvLogService;
    private final MixedPanelSummaryService mixedPanelSummaryService;
    private final PageTotalSuccessService pageTotalSuccessService;
    private final MmBounceRateFeignClient mmBounceRateFeignClient;
    private final PublishFeignClient publishFeignClient;
    private final AssemblyManager assemblyManager;

    private final LoadingCache<String, Long> BOUNCE_RATE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(50)
            .concurrencyLevel(8)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    log.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public Long load(@NotNull String mmCode) {
                    MmBounceRate rate = mmBounceRateFeignClient.getByMmCode(mmCode).getData();
                    return rate == null ? 30000L : rate.getThreshold();
                }
            });


    @Override
    public void saveMixedPanelSummary(Date time) {
        //避免重复触发
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        long count = mixedPanelSummaryService.count(new LambdaQueryWrapper<MixedPanelSummary>().eq(MixedPanelSummary::getDate, DateUtil.format(time, "yyyy-MM-dd")).last("limit 1"));
        Assert.notTrue(count > 0, StatStatusCode.REPEATED_TRIGGER);
        List<PageViewLog> pvList = pageViewLogService.list(new LambdaQueryWrapper<PageViewLog>().eq(PageViewLog::getEventDate, DateUtil.format(time, "yyyy-MM-dd")));
        // 汇总数据，进行组装后插入summary
        Map<String, MixedPanelSummary> map = new HashMap<>();
        HashSet<String> uuidFilterSet = new HashSet<>();
        HashSet<String> bizIdFilterSet = new HashSet<>();
        pvList.forEach(pv -> {
            MixedPanelSummary mixedPanelSummary = map.get(getMixedPanelGroupKey(pv));
            if (mixedPanelSummary == null) {
                mixedPanelSummary = new MixedPanelSummary()
                        .setDate(time)
                        .setMmCode(pv.getMmCode())
                        .setPageNo(pv.getPageNo())
                        .setFr(CustomerTypeEnum.judge(pv.getMemberType(), "FR"))
                        .setNfr(CustomerTypeEnum.judge(pv.getMemberType(), "NFR"))
                        .setHo(CustomerTypeEnum.judge(pv.getMemberType(), "HO"))
                        .setSv(CustomerTypeEnum.judge(pv.getMemberType(), "SV"))
                        .setDt(CustomerTypeEnum.judge(pv.getMemberType(), "DT"))
                        .setOt(CustomerTypeEnum.judge(pv.getMemberType(), "OT"))
                        .setChannel(pv.getChannel())
                        .setMobile(ObjectUtil.equals(pv.getMobile(), 1))
                        .setPlatform(pv.getPlatform())
                        .setMemberToMm(0L)
                        .setSessionToMm(0L)
                        .setSessionToMmPage(0L)
                        .setMemberToMmPage(0L)
                        .setToPage(0L)
                        .setToItem(0L)
                        .setStayTime(0L)
                        .setBounceRateCounts(0L);
                map.put(getMixedPanelGroupKey(pv), mixedPanelSummary);
            }
            if (bizIdFilterSet.add(pv.getBizId())) {
                mixedPanelSummary.setSessionToMm(mixedPanelSummary.getSessionToMm() + 1);
            }
            if (bizIdFilterSet.add(pv.getPageNo() + pv.getBizId())) {
                mixedPanelSummary.setSessionToMmPage(mixedPanelSummary.getSessionToMmPage() + 1);
            }
            if (uuidFilterSet.add(pv.getUuid())) {
                mixedPanelSummary.setMemberToMm(mixedPanelSummary.getMemberToMm() + 1);
            }
            if (uuidFilterSet.add(pv.getPageNo() + pv.getUuid())) {
                mixedPanelSummary.setMemberToMmPage(mixedPanelSummary.getMemberToMmPage() + 1);
            }
            mixedPanelSummary.setToPage(mixedPanelSummary.getToPage() + 1);
        });


        List<GoodsClickLog> gcList = goodsClickLogService.list(new LambdaQueryWrapper<GoodsClickLog>().eq(GoodsClickLog::getEventDate, DateUtil.format(time, "yyyy-MM-dd")));
        gcList.forEach(gc -> {
            MixedPanelSummary mixedPanelSummary = map.get(getMixedPanelGroupKey(gc));
            if (mixedPanelSummary == null) {
                log.info("gcList 存在pv没有数据 gc:{}", JSON.toJSONString(gc));
                return;
            }
            mixedPanelSummary.setToItem(mixedPanelSummary.getToItem() + 1);
        });


        List<PageStayLog> psList = pageStayLogService.list(new QueryWrapper<PageStayLog>()
                .select("sum(stay_time) as stay_time,mm_code,page_no,member_type,channel,platform")
                .eq("event_date", DateUtil.format(time, "yyyy-MM-dd"))
                .groupBy("biz_id,mm_code,page_no,member_type,channel,platform"));
        psList.forEach(ps -> {
            MixedPanelSummary mixedPanelSummary = map.get(getMixedPanelGroupKey(ps));
            if (mixedPanelSummary == null) {
                log.info("psList 存在pv没有数据 gc:{}", JSON.toJSONString(ps));
                return;
            }
            Long threshold;
            try {
                threshold = BOUNCE_RATE_CACHE.get(ps.getMmCode());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            mixedPanelSummary.setStayTime(mixedPanelSummary.getStayTime() + ps.getStayTime());
            if (ps.getStayTime() > threshold) {
                mixedPanelSummary.setBounceRateCounts(mixedPanelSummary.getBounceRateCounts() + 1);
            }
        });


        mixedPanelSummaryService.saveBatch(map.values());
    }

    @Override
    public void pageTotalSuccess(Date time) {
        //避免重复触发
        log.info("执行pageTotalSuccess stat time:{}", time);
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        long count = pageTotalSuccessService.count(new LambdaQueryWrapper<PageTotalSuccess>().eq(PageTotalSuccess::getDate, DateUtil.format(time, "yyyy-MM-dd")).last("limit 1"));
        Assert.notTrue(count > 0, StatStatusCode.REPEATED_TRIGGER);
        List<PageTotalSuccess> data = publishFeignClient.pageTotalSuccess(time).getData();
        pageTotalSuccessService.saveBatch(data);
    }

    private String getMixedPanelGroupKey(PageStayLog log) {
        return log.getMmCode() + log.getPageNo() + CustomerTypeEnum.getCustomerType(log.getMemberType()) + log.getChannel() + log.getPlatform();
    }

    private String getMixedPanelGroupKey(GoodsClickLog log) {
        return log.getMmCode() + log.getPageNo() + CustomerTypeEnum.getCustomerType(log.getMemberType()) + log.getChannel() + log.getPlatform();
    }

    private String getMixedPanelGroupKey(PageViewLog log) {
        return log.getMmCode() + log.getPageNo() + CustomerTypeEnum.getCustomerType(log.getMemberType()) + log.getChannel() + log.getPlatform();
    }


    /**
     * 功能描述: 保存某个mm的pv uv mv
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/4 用户行为分析
     */
    @Override
    public void saveBehaviorData(Date time) {
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(behaviorDataService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);
        List<BehaviorData> behaviorData = assemblyManager.getBehaviorData(time);
        behaviorDataService.saveBehaviorData(behaviorData, time);
    }


    /**
     * 功能描述: 按照MemberType分类 统计goods_click_log pv
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/18 用户行为分析
     */
    @Override
    public void saveVisitorClicksOnProduct(Date time) {
        //避免重复触发
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(visitorClicksOnProductService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);

        List<VisitorClicksOnProduct> all = goodsClickLogService.selectTimeVisitorClicksOnProduct("All", time);
        //保存并将今天所有数据生成一条总数
        VisitorClicksOnProduct allProduct = new VisitorClicksOnProduct();
        allProduct.setMmCode("0");
        allProduct.setProductClick(all.stream().mapToLong(VisitorClicksOnProduct::getProductClick).sum());
        all.add(allProduct);
        visitorClicksOnProductService.saveVisitorClicksOnProduct(all, "All", time);


        List<VisitorClicksOnProduct> member = goodsClickLogService.selectTimeVisitorClicksOnProduct("Member", time);
        //保存并将今天所有数据生成一条总数
        VisitorClicksOnProduct memberProduct = new VisitorClicksOnProduct();
        memberProduct.setMmCode("0");
        memberProduct.setProductClick(member.stream().mapToLong(VisitorClicksOnProduct::getProductClick).sum());
        member.add(memberProduct);
        visitorClicksOnProductService.saveVisitorClicksOnProduct(member, "Member", time);
    }

    /**
     * 功能描述:
     * 按照channel分类 统计page_view_log pv/mv
     * 除了APP其他都是H5
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/18 用户行为分析
     */
    @Override
    public void averageVisitorVisits(Date time) {
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(averageVisitorVisitsService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);

        List<AverageVisitorVisitsPvMvDTO> appList = pageViewLogService.selectTimeAverageVisitorVisits("APP", time);
        List<AverageVisitorVisitsPvMvDTO> appMvList = pageViewLogService.selectTimeAverageVisitorVisitsMv("APP", time);
        List<AverageVisitorVisits> apps = new ArrayList<>();
        appList.forEach(x -> appMvList.forEach(y -> {
            if (Objects.equals(x.getMmCode(), y.getMmCode())) {
                AverageVisitorVisits visits = new AverageVisitorVisits();
                visits.setMmCode(x.getMmCode());
                visits.setChannel("APP");
                visits.setPvDivideMv(x.getPv() / y.getMv());
                apps.add(visits);
            }
        }));

        //保存并将今天所有数据生成一条总数
        AverageVisitorVisits app = new AverageVisitorVisits();
        app.setMmCode("0");
        app.setPvDivideMv(apps.stream().mapToDouble(AverageVisitorVisits::getPvDivideMv).average().orElse(0));
        apps.add(app);
        averageVisitorVisitsService.saveVisitorClicksOnProduct(apps, "APP", time);


        List<AverageVisitorVisitsPvMvDTO> h5List = pageViewLogService.selectTimeAverageVisitorVisits("H5", time);
        List<AverageVisitorVisitsPvMvDTO> h5MvList = pageViewLogService.selectTimeAverageVisitorVisitsMv("H5", time);
        List<AverageVisitorVisits> h5s = new ArrayList<>();
        h5List.forEach(x -> h5MvList.forEach(y -> {
            if (Objects.equals(x.getMmCode(), y.getMmCode())) {
                AverageVisitorVisits visits = new AverageVisitorVisits();
                visits.setMmCode(x.getMmCode());
                visits.setChannel("H5");
                visits.setPvDivideMv(x.getPv() / y.getMv());
                h5s.add(visits);
            }
        }));

        //保存并将今天所有数据生成一条总数
        AverageVisitorVisits h5 = new AverageVisitorVisits();
        h5.setMmCode("0");
        h5.setPvDivideMv(h5s.stream().mapToDouble(AverageVisitorVisits::getPvDivideMv).average().orElse(0));
        h5s.add(h5);
        averageVisitorVisitsService.saveVisitorClicksOnProduct(h5s, "H5", time);


        List<AverageVisitorVisitsPvMvDTO> allList = pageViewLogService.selectTimeAverageVisitorVisits("All", time);
        List<AverageVisitorVisitsPvMvDTO> allMvList = pageViewLogService.selectTimeAverageVisitorVisitsMv("All", time);
        List<AverageVisitorVisits> alls = new ArrayList<>();
        allList.forEach(x -> allMvList.forEach(y -> {
            if (Objects.equals(x.getMmCode(), y.getMmCode())) {
                AverageVisitorVisits visits = new AverageVisitorVisits();
                visits.setMmCode(x.getMmCode());
                visits.setChannel("All");
                visits.setPvDivideMv(x.getPv() / y.getMv());
                alls.add(visits);
            }
        }));

        //保存并将今天所有数据生成一条总数
        AverageVisitorVisits all = new AverageVisitorVisits();
        all.setMmCode("0");
        all.setPvDivideMv(alls.stream().mapToDouble(AverageVisitorVisits::getPvDivideMv).average().orElse(0));
        alls.add(all);
        averageVisitorVisitsService.saveVisitorClicksOnProduct(alls, "All", time);

    }

    /**
     * 功能描述: 按照channel分类 统计page_view_log uv
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/18 用户行为分析
     */
    @Override
    public void channelVisitorConversion(Date time) {
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(channelVisitorConversionService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);
        List<ChannelVisitorConversion> result = assemblyManager.getChannelVisitorConversion(time);
        if (CollectionUtil.isNotEmpty(result)) {
            channelVisitorConversionService.saveChannelVisitorConversion(result, time);
        }
    }



    /**
     * 功能描述: 按照MemberType分类 统计page_view_log uv
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/18 用户行为分析
     */
    @Override
    public void memberTypeClickThroughRate(Date time) {
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(assemblyDataByMemberTypeService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);
        ArrayList<AssemblyDataByMemberType> results = assemblyManager.getAssemblyDataByMemberType(time);
        if (CollectionUtil.isNotEmpty(results)) {
            assemblyDataByMemberTypeService.saveMemberTypeClickThroughRate(results, time);
        }
    }

    /**
     * 功能描述: 按照goodsCode分类 统计goods_click_log  pv uv
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/18 用户行为分析
     */
    @Override
    public void productAnalysis(Date time) {
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(productAnalysisService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);
        List<ProductAnalysis> result = assemblyManager.getProductAnalyses(time);
        productAnalysisService.saveBatch(result);
    }

    @Override
    public void visitsAnalysis(Date calTime) {
        processAnalysis(visitsCalCountService::hasTargetHourData, pageViewLogService::listVisitSummary, calTime, "visit");
    }

    @Override
    public void visitorsAnalysis(Date calTime) {
        log.info("visitorsAnalysis job date:{}", calTime);
        processAnalysis(visitorCalCountService::hasTargetHourData, pageViewLogService::listVisitorSummary, calTime, "visitor");
    }

    @Override
    public void mostVisitPage(Date time) {
        //避免重复触发
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        Assert.notTrue(pagePageNoService.hasTimeData(time), StatStatusCode.REPEATED_TRIGGER);
        List<PagePageNo> result = assemblyManager.getPagePageNo(time);
        pagePageNoService.saveBatch(result);

    }

    @Override
    public void friends(Date time) {
        //避免重复触发
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        long count = pageChannelMemberTypeService.count(new LambdaQueryWrapper<PageChannelMemberType>().eq(PageChannelMemberType::getDate, DateUtil.format(time, "yyyy-MM-dd")).last("limit 1"));
        Assert.notTrue(count > 0, StatStatusCode.REPEATED_TRIGGER);
        List<PageChannelMemberType> result = assemblyManager.getPageChannelMemberType(time);
        pageChannelMemberTypeService.saveBatch(result);
    }

    @Override
    public void stayPageNo(Date time) {
        //避免重复触发
        Assert.notTrue(Objects.isNull(time), StatStatusCode.INPUT_TIME_IS_NULL);
        long count = stayPageNoService.count(new LambdaQueryWrapper<StayPageNo>().eq(StayPageNo::getDate, DateUtil.format(time, "yyyy-MM-dd")).last("limit 1"));
        Assert.notTrue(count > 0, StatStatusCode.REPEATED_TRIGGER);

        QueryWrapper<PageStayLog> wrapper = new QueryWrapper<PageStayLog>()
                .select("mm_code as mmCode ,sum(stay_time) as stayTime,page_no")
                .eq("event_date", DateUtil.format(time, "yyyy-MM-dd"))
                .groupBy("mm_code,page_no");
        List<PageStayLog> all = pageStayLogService.list(wrapper);

        List<StayPageNo> result = packagingStayMmCode(time, all);

        stayPageNoService.saveBatch(result);
    }

    private List<StayPageNo> packagingStayMmCode(Date time, List<PageStayLog> all) {
        return all.stream().map(x -> {
            StayPageNo stayPageNo = new StayPageNo();
            BeanUtil.copyProperties(x, stayPageNo);
            stayPageNo.setDate(time);
            return stayPageNo;
        }).collect(Collectors.toList());
    }





    private void processAnalysis(Function<String, Boolean> checkFunctions, BiFunction<Date, Date, List<VisitSummaryDTO>> summaryFunction,
                                 Date calTime, String type) {
        CalTimeDTO calTimeDTO = getCalTimeDTO(calTime, checkFunctions);
        switch (type) {
            case "visit":
                generateVisitData(calTimeDTO.getCalDate(), calTimeDTO.getCalHour(), summaryFunction.apply(calTimeDTO.getBegin(), calTimeDTO.getEnd()));
                break;
            case "visitor":
                generateVisitorData(calTimeDTO.getCalDate(), calTimeDTO.getCalHour(), summaryFunction.apply(calTimeDTO.getBegin(), calTimeDTO.getEnd()));
                break;
            default:
                break;
        }
    }

    public void generateVisitData(Date calDate, String calHour, List<VisitSummaryDTO> summaryDTOS) {
        summaryDTOS.forEach(a -> {
            VisitsCalCount count = VisitsCalCount.builder().calDate(calDate).calHour(calHour).createTime(new Date())
                    .mmCode(a.getMmCode()).num(a.getTotal()).build();
            visitsCalCountService.save(count);
        });
    }

    public void generateVisitorData(Date calDate, String calHour, List<VisitSummaryDTO> summaryDTOS) {
        summaryDTOS.forEach(a -> {
            VisitorCalCount count = VisitorCalCount.builder().calDate(calDate).calHour(calHour).createTime(new Date())
                    .mmCode(a.getMmCode()).num(a.getTotal()).build();
            visitorCalCountService.save(count);
        });
    }

    @Override
    public void assemblePageStay(Date calTime) {
        CalTimeDTO calTimeDTO = getCalTimeDTO(calTime, pageStaySummaryService::hasTargetHourData);
        List<PageStayLog> stayLogs = pageStayLogService.list(new LambdaQueryWrapper<PageStayLog>()
                        .between(PageStayLog::getTs, DateUtil.format(calTimeDTO.getBegin(), "yyyy-MM-dd HH:mm:ss"),
                                DateUtil.format(calTimeDTO.getEnd(), "yyyy-MM-dd HH:mm:ss"))).stream()
                .filter(x -> StrUtil.isNotBlank(x.getMemberType()) && StrUtil.isNotBlank(x.getChannel())).collect(Collectors.toList());
        // 汇总数据，进行组装后插入summary
        Map<String, PageStaySummary> map = getPageStaySummaryMap(calTimeDTO.getCalDate(), calTimeDTO.getCalHour(), stayLogs);
        map.forEach((key, value) -> pageStaySummaryService.save(value));
    }

    @Override
    public Map<String, PageStaySummary> getPageStaySummaryMap(Date calDate, String calHour, List<PageStayLog> stayLogs) {
        Map<String, PageStaySummary> map = new HashMap<>();
        HashSet<String> uuidFilterSet = new HashSet<String>();
        stayLogs.forEach(stayLog -> {
            PageStaySummary pageStaySummary = map.get(getGroupKey(stayLog));
            try {
                Long threshold = BOUNCE_RATE_CACHE.get(stayLog.getMmCode());
                if (pageStaySummary == null) {
                    pageStaySummary = PageStaySummary.builder().mmCode(stayLog.getMmCode())
                            .pageNo(stayLog.getPageNo()).calDate(calDate).calHour(calHour)
                            .channel(stayLog.getChannel()).memberType(stayLog.getMemberType())
                            .storeCode(stayLog.getStoreCode()).visits(0L).visitors(1L).newVisitors(0L)
                            .stayTime(stayLog.getStayTime())
                            .bounceRateCounts(0L)
                            .createTime(new Date())
                            .build();
                    setCustomerType(stayLog, pageStaySummary);
                    map.put(getGroupKey(stayLog), pageStaySummary);
                } else {
                    pageStaySummary.setStayTime(pageStaySummary.getStayTime() + stayLog.getStayTime());
                }
                pageStaySummary.setVisits(pageStaySummary.getVisits() + 1);
                if (uuidFilterSet.add(stayLog.getUuid())) {
                    pageStaySummary.setVisitors(pageStaySummary.getVisitors() + 1);
                }
                if (Objects.equals(1, stayLog.getIsNew())) {
                    pageStaySummary.setNewVisitors(pageStaySummary.getNewVisitors() + 1);
                }
                if (stayLog.getStayTime() > threshold) {
                    pageStaySummary.setBounceRateCounts(pageStaySummary.getBounceRateCounts() + 1);
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return map;
    }

    private void setCustomerType(PageStayLog stayLog, PageStaySummary pageStaySummary) {
        if (StrUtil.isNotBlank(stayLog.getMemberType())) {
            for (String s : stayLog.getMemberType().split(",")) {
                switch (CustomerTypeEnum.getNameByMemberType(s)) {
                    case "FR":
                        pageStaySummary.setFr(true);
                        break;
                    case "NFR":
                        pageStaySummary.setNfr(true);
                        break;
                    case "HO":
                        pageStaySummary.setHo(true);
                        break;
                    case "SV":
                        pageStaySummary.setSv(true);
                        break;
                    case "DT":
                        pageStaySummary.setDt(true);
                        break;
                    case "OT":
                        pageStaySummary.setOt(true);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    private String getGroupKey(PageStayLog stayLog) {
        return stayLog.getMmCode() + stayLog.getPageNo() + stayLog.getMemberType() + stayLog.getChannel() + stayLog.getStoreCode();
    }

    private CalTimeDTO getCalTimeDTO(Date calTime, Function<String, Boolean> checkFunctions) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNullElseGet(calTime, Date::new));
        Date calDate = calendar.getTime();
        calendar.add(HOUR_OF_DAY, -1);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String calHour = (hour < 10 ? "0" + hour : hour) + ":00";
        Assert.notTrue(checkFunctions.apply(calHour), StatStatusCode.REPEATED_TRIGGER);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date begin = calendar.getTime();
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date end = calendar.getTime();
        return CalTimeDTO.builder().calDate(calDate).calHour(calHour).begin(begin).end(end).build();
    }

    @Data
    @Builder
    public static class CalTimeDTO {

        private Date calDate;

        private String calHour;

        private Date begin;

        private Date end;

    }

}
