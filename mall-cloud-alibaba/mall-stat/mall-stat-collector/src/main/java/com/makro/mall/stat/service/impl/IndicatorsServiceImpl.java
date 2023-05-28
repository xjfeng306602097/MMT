package com.makro.mall.stat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.model.StatStatusCode;
import com.makro.mall.stat.manager.AssemblyManager;
import com.makro.mall.stat.pojo.constant.CustomerTypeEnum;
import com.makro.mall.stat.pojo.dto.*;
import com.makro.mall.stat.pojo.metadata.PageStayLog;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.pojo.vo.*;
import com.makro.mall.stat.service.*;
import com.makro.mall.template.api.TemplateFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 功能描述:  用于数据拆分
 * 用户行为分析
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorsServiceImpl implements IndicatorsService {

    private final BehaviorDataService behaviorDataService;
    private final VisitorClicksOnProductService visitorClicksOnProductService;
    private final AverageVisitorVisitsService averageVisitorVisitsService;
    private final ChannelVisitorConversionService channelVisitorConversionService;
    private final ProductAnalysisService productAnalysisService;
    private final PageViewLogService pageViewLogService;
    private final AssemblyDataByMemberTypeService assemblyDataByMemberTypeService;
    private final VisitorCalCountService visitorCalCountService;
    private final VisitsCalCountService visitsCalCountService;
    private final MmActivityFeignClient mmActivityFeignClient;
    private final CustomerFeignClient customerFeignClient;
    private final PagePageNoService pagePageNoService;
    private final PageChannelMemberTypeService pageChannelMemberTypeService;
    private final PageStaySummaryService pageStaySummaryService;
    private final StayPageNoService stayPageNoService;
    private final PageTotalSuccessService pageTotalSuccessService;
    private final MixedPanelSummaryService mixedPanelSummaryService;
    private final TemplateFeignClient templateFeignClient;
    private final AssemblyManager assemblyManager;

    private final PageStayLogService pageStayLogService;

    private final AssemblyService assemblyService;

    @Override
    public IPage<ProductAnalysis> itemClicks(Date startTime, Date endTime, SortPageRequest<ProductAnalysisDTO> req) {
        String sortSql = req.getSortSql();
        ProductAnalysisDTO productAnalysis = req.getReq();
        MakroPage<ProductAnalysis> page = productAnalysisService.page(new MakroPage<>(req.getPage(), req.getLimit()), new QueryWrapper<ProductAnalysis>()
                .select("goods_code,name_en,name_thai,page_no,sum(clicks) as clicks,sum(visitors) as visitors")
                .eq("mm_code", productAnalysis.getMmCode())
                .between("date", DateUtil.format(startTime, "yyyy-MM-dd"), DateUtil.format(endTime, "yyyy-MM-dd"))
                .in(StrUtil.isNotBlank(productAnalysis.getPageNo()), "page_no", StrUtil.split(productAnalysis.getPageNo(), ","))
                .in(CollUtil.isNotEmpty(productAnalysis.getChannel()), "channel", productAnalysis.getChannel())
                .like(StrUtil.isNotBlank(productAnalysis.getNameThai()), "name_thai", productAnalysis.getNameThai())
                .like(StrUtil.isNotBlank(productAnalysis.getNameEn()), "name_en", productAnalysis.getNameEn())
                .like(StrUtil.isNotBlank(productAnalysis.getGoodsCode()), "goods_code", productAnalysis.getGoodsCode())
                .groupBy("goods_code,name_en,name_thai,page_no")
                .last(sortSql)
        );

        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = productAnalysis.getMmCode();
            List<ProductAnalysis> collect = assemblyManager.getProductAnalyses(endTime).stream().filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode)).collect(Collectors.toList());
            List<ProductAnalysis> productAnalyses = collect.stream()
                    .filter(x -> StrUtil.equals(finalMmCode, x.getMmCode()))
                    .filter(x -> {
                        if (StrUtil.isNotBlank(productAnalysis.getPageNo())) {
                            return CollUtil.contains(StrUtil.split(productAnalysis.getPageNo(), ","), x.getPageNo());
                        } else {
                            return true;
                        }
                    })
                    .filter(x -> {
                        if (CollUtil.isNotEmpty(productAnalysis.getChannel())) {
                            return CollUtil.contains(productAnalysis.getChannel(), x.getChannel());
                        } else {
                            return true;
                        }
                    })
                    .filter(x -> {
                        if (StrUtil.isNotEmpty(productAnalysis.getNameThai())) {
                            return StrUtil.contains(x.getNameThai(), productAnalysis.getNameThai());
                        } else {
                            return true;
                        }
                    })
                    .filter(x -> {
                        if (StrUtil.isNotEmpty(productAnalysis.getNameEn())) {
                            return StrUtil.contains(x.getNameEn(), productAnalysis.getNameEn());
                        } else {
                            return true;
                        }
                    })
                    .filter(x -> {
                        if (StrUtil.isNotEmpty(productAnalysis.getGoodsCode())) {
                            return StrUtil.contains(x.getGoodsCode(), productAnalysis.getGoodsCode());
                        } else {
                            return true;
                        }
                    })
                    .collect(Collectors.toList());
            Map<String, Long> clicksMap = productAnalyses.stream().collect(Collectors.groupingBy(x -> x.getGoodsCode() + "#" + x.getNameEn() + "#" + x.getNameThai() + "#" + x.getPageNo(), Collectors.summingLong(ProductAnalysis::getClicks)));
            Map<String, Long> visitorsMap = productAnalyses.stream().collect(Collectors.groupingBy(x -> x.getGoodsCode() + "#" + x.getNameEn() + "#" + x.getNameThai() + "#" + x.getPageNo(), Collectors.summingLong(ProductAnalysis::getVisitors)));

            List<ProductAnalysis> records = clicksMap.keySet().stream().map(key -> {
                String[] arr = key.split("#");
                String goodsCode = arr[0];
                String nameEn = arr[1];
                String nameThai = arr[2];
                String pageNo = arr[3];
                ProductAnalysis obj = new ProductAnalysis();
                obj.setGoodsCode(goodsCode);
                obj.setNameEn(nameEn);
                obj.setNameThai(nameThai);
                obj.setPageNo(pageNo);
                obj.setClicks(clicksMap.get(key));
                obj.setVisitors(visitorsMap.get(key));
                return obj;
            }).collect(Collectors.toList());
            if (CollUtil.isEmpty(page.getRecords())) {
                //自己组装分页
                MakroPage<ProductAnalysis> page1 = new MakroPage<>(req.getPage(), req.getLimit());
                page1.setRecords(records);
                page1.setTotal(records.size());
                return page1;
            } else {
                //把今天+昨天的累加
                page.getRecords().forEach(x -> {
                    x.setClicks(x.getClicks() + records.stream()
                            .filter(y -> StrUtil.equals(x.getPageNo(), y.getPageNo()))
                            .filter(y -> StrUtil.equals(x.getNameThai(), y.getNameThai()))
                            .filter(y -> StrUtil.equals(x.getNameEn(), y.getNameEn()))
                            .filter(y -> StrUtil.equals(x.getGoodsCode(), y.getGoodsCode()))
                            .mapToLong(ProductAnalysis::getClicks)
                            .sum());
                    x.setVisitors(x.getVisitors() + records.stream()
                            .filter(y -> StrUtil.equals(x.getPageNo(), y.getPageNo()))
                            .filter(y -> StrUtil.equals(x.getNameThai(), y.getNameThai()))
                            .filter(y -> StrUtil.equals(x.getNameEn(), y.getNameEn()))
                            .filter(y -> StrUtil.equals(x.getGoodsCode(), y.getGoodsCode()))
                            .mapToLong(ProductAnalysis::getVisitors)
                            .sum());
                });
            }
        }
        return page;
    }

    @Override
    public BasicDataVO basicData(String mmCode) {
        BasicDataVO vo = new BasicDataVO();
        BasicDataDTO.BasicData yesterdayPvBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData yesterdayUvBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData weekPvBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData weekUvBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData fourteenPvDayBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData fourteenUvDayBasicData = new BasicDataDTO.BasicData();

        DateTime yesterday = DateUtil.yesterday();
        DateTime beforeYesterday = DateUtil.offsetDay(DateTime.now(), -2);
        DateTime lastWeek = DateUtil.offsetDay(DateTime.now(), -8);
        DateTime preLastWeek = DateUtil.offsetDay(DateTime.now(), -15);
        DateTime last14Days = DateUtil.offsetDay(DateTime.now(), -15);
        DateTime last28Days = DateUtil.offsetDay(DateTime.now(), -29);

        // 查询昨天的数据
        List<BehaviorData> yesterdayList = behaviorDataService.list(mmCode, yesterday, yesterday);
        List<BehaviorData> beforeYesterdayList = behaviorDataService.list(mmCode, beforeYesterday, beforeYesterday);
        // 昨天PV,UV数据统计
        processPvUv(yesterdayPvBasicData, yesterdayUvBasicData, yesterdayList, beforeYesterdayList);
        // 查询上周的数据
        List<BehaviorData> lastWeekList = behaviorDataService.list(mmCode, lastWeek, yesterday);
        List<BehaviorData> beforeLastWeekList = behaviorDataService.list(mmCode, preLastWeek, DateUtil.offsetDay(lastWeek, -1));
        // 上周PV,UV数据统计
        processPvUv(weekPvBasicData, weekUvBasicData, lastWeekList, beforeLastWeekList);
        // 查询前14天的数据
        List<BehaviorData> last14DaysList = behaviorDataService.list(mmCode, last14Days, yesterday);
        List<BehaviorData> last28DaysList = behaviorDataService.list(mmCode, last28Days, DateUtil.offsetDay(last14Days, -1));
        // 14天PV,UV数据统计
        processPvUv(fourteenPvDayBasicData, fourteenUvDayBasicData, last14DaysList, last28DaysList);

        // 生成商品点击数据
        BasicDataDTO.BasicData yesterdayGoodsClickBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData weekGoodsClickBasicData = new BasicDataDTO.BasicData();
        BasicDataDTO.BasicData fourteenGoodsClickDayBasicData = new BasicDataDTO.BasicData();
        // 查询昨天的数据
        List<ProductAnalysis> pYesterdayList = productAnalysisService.list(mmCode, yesterday, yesterday, null, null, null);
        List<ProductAnalysis> beforePYesterdayList = productAnalysisService.list(mmCode, beforeYesterday, beforeYesterday, null, null, null);
        processGoodsClick(yesterdayGoodsClickBasicData, pYesterdayList, beforePYesterdayList);

        List<ProductAnalysis> pWeekList = productAnalysisService.list(mmCode, lastWeek, yesterday, null, null, null);
        List<ProductAnalysis> beforePWeekList = productAnalysisService.list(mmCode, preLastWeek, DateUtil.offsetDay(lastWeek, -1), null, null, null);
        processGoodsClick(weekGoodsClickBasicData, pWeekList, beforePWeekList);

        List<ProductAnalysis> pLast14DaysList = productAnalysisService.list(mmCode, last14Days, yesterday, null, null, null);
        List<ProductAnalysis> pLast28DaysList = productAnalysisService.list(mmCode, last28Days, DateUtil.offsetDay(last14Days, -1), null, null, null);
        processGoodsClick(fourteenGoodsClickDayBasicData, pLast14DaysList, pLast28DaysList);

        BasicDataDTO pv = BasicDataDTO.builder().generate(BasicDataDTO.BasicDataEnum.YESTERDAY.getCode(), yesterdayPvBasicData)
                .generate(BasicDataDTO.BasicDataEnum.WEEK.getCode(), weekPvBasicData)
                .generate(BasicDataDTO.BasicDataEnum.LAST_14_DAYS.getCode(), fourteenPvDayBasicData)
                .build();
        BasicDataDTO uv = BasicDataDTO.builder().generate(BasicDataDTO.BasicDataEnum.YESTERDAY.getCode(), yesterdayUvBasicData)
                .generate(BasicDataDTO.BasicDataEnum.WEEK.getCode(), weekUvBasicData)
                .generate(BasicDataDTO.BasicDataEnum.LAST_14_DAYS.getCode(), fourteenUvDayBasicData)
                .build();
        BasicDataDTO goodsClick = BasicDataDTO.builder().generate(BasicDataDTO.BasicDataEnum.YESTERDAY.getCode(), yesterdayGoodsClickBasicData)
                .generate(BasicDataDTO.BasicDataEnum.WEEK.getCode(), weekGoodsClickBasicData)
                .generate(BasicDataDTO.BasicDataEnum.LAST_14_DAYS.getCode(), fourteenGoodsClickDayBasicData)
                .build();

        vo.setPv(pv);
        vo.setUv(uv);
        vo.setProductClick(goodsClick);
        vo.setLastUpdateTime(yesterday);

        return vo;
    }

    private void processGoodsClick(BasicDataDTO.BasicData goodsClickBasicData, List<ProductAnalysis> list, List<ProductAnalysis> preList) {
        if (CollectionUtil.isNotEmpty(list)) {
            long clickCount = 0;
            for (ProductAnalysis productAnalysis : list) {
                clickCount += productAnalysis.getClicks();
            }
            goodsClickBasicData.setCount(clickCount);
        }
        if (CollectionUtil.isNotEmpty(preList)) {
            long preCount = 0;
            for (ProductAnalysis productAnalysis : preList) {
                preCount += productAnalysis.getClicks();
            }
            goodsClickBasicData.setChain(goodsClickBasicData.getCount() - preCount);
            goodsClickBasicData.setChainRatio(new BigDecimal(goodsClickBasicData.getChain()).divide(new BigDecimal(preCount), 3, RoundingMode.HALF_UP));
        } else {
            goodsClickBasicData.setChain(goodsClickBasicData.getCount());
        }
    }

    private void processPvUv(BasicDataDTO.BasicData yesterdayPvBasicData, BasicDataDTO.BasicData yesterdayUvBasicData, List<BehaviorData> yesterdayList, List<BehaviorData> beforeYesterdayList) {
        if (CollectionUtil.isNotEmpty(yesterdayList)) {
            long pvCount = 0;
            long uvCount = 0;
            for (BehaviorData behaviorData : yesterdayList) {
                pvCount += behaviorData.getPv();
                uvCount += behaviorData.getUv();
            }
            yesterdayPvBasicData.setCount(pvCount);
            yesterdayUvBasicData.setCount(uvCount);
        }
        if (CollectionUtil.isNotEmpty(beforeYesterdayList)) {
            long pvCount = 0;
            long uvCount = 0;
            for (BehaviorData behaviorData : beforeYesterdayList) {
                pvCount += behaviorData.getPv();
                uvCount += behaviorData.getUv();
            }
            yesterdayPvBasicData.setChain(yesterdayPvBasicData.getCount() - pvCount);
            yesterdayPvBasicData.setChainRatio(pvCount == 0 ? BigDecimal.ZERO : new BigDecimal(yesterdayPvBasicData.getChain()).divide(new BigDecimal(pvCount), 3, RoundingMode.HALF_UP));
            yesterdayUvBasicData.setChain(yesterdayUvBasicData.getCount() - uvCount);
            yesterdayUvBasicData.setChainRatio(uvCount == 0 ? BigDecimal.ZERO : new BigDecimal(yesterdayUvBasicData.getChain()).divide(new BigDecimal(uvCount), 3, RoundingMode.HALF_UP));
        } else {
            yesterdayPvBasicData.setChain(yesterdayPvBasicData.getCount());
            yesterdayUvBasicData.setChain(yesterdayUvBasicData.getCount());
        }
    }

    @Override
    public RealTimeAccessVO realTimeAccess(String mmCode, Date queryDate) {
        RealTimeAccessVO realTimeAccessVO = new RealTimeAccessVO();
        realTimeAccessVO.setVisits(getRealTimeAccessVisits(mmCode, queryDate));
        realTimeAccessVO.setVisitors(getRealTimeAccessVisitors(mmCode, queryDate));
        return realTimeAccessVO;
    }

    /**
     * 功能描述:实时访问人数
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/21
     */
    private LineChartVO getRealTimeAccessVisitors(String mmCode, Date queryDate) {
        LineChartVO lineChartVO = new LineChartVO();
        List<String> list = ListUtil.of("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        lineChartVO.setLabel(list);
        Calendar calendar = Calendar.getInstance();
        if (Objects.isNull(queryDate)) {
            lineChartVO.setUpdateTime(new Date());
            calendar.setTime(new Date());
            lineChartVO.getList().add(generateMetadataDTO("Today", visitorCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            lineChartVO.getList().add(generateMetadataDTO("1 day ago", visitorCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
            calendar.add(Calendar.DAY_OF_YEAR, -6);
            lineChartVO.getList().add(generateMetadataDTO("7 days ago", visitorCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
        } else {
            lineChartVO.setUpdateTime(queryDate);
            calendar.setTime(queryDate);
            lineChartVO.getList().add(generateMetadataDTO(DateUtil.format(queryDate, "yyyy-MM-dd"), visitorCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
        }
        return lineChartVO;
    }

    /**
     * 功能描述:获取实时访问次数
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/21
     */
    public LineChartVO getRealTimeAccessVisits(String mmCode, Date queryDate) {
        LineChartVO lineChartVO = new LineChartVO();
        List<String> list = ListUtil.of("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        lineChartVO.setLabel(list);
        Calendar calendar = Calendar.getInstance();
        if (Objects.isNull(queryDate)) {
            lineChartVO.setUpdateTime(new Date());
            calendar.setTime(new Date());
            lineChartVO.getList().add(generateVisitsMetadataDTO("Today", visitsCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            lineChartVO.getList().add(generateVisitsMetadataDTO("1 day ago", visitsCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
            calendar.add(Calendar.DAY_OF_YEAR, -6);
            lineChartVO.getList().add(generateVisitsMetadataDTO("7 days ago", visitsCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
        } else {
            lineChartVO.setUpdateTime(queryDate);
            calendar.setTime(queryDate);
            lineChartVO.getList().add(generateVisitsMetadataDTO(DateUtil.format(queryDate, "yyyy-MM-dd"), visitsCalCountService.listByCodeAndTime(mmCode, calendar.getTime()), list));
        }
        return lineChartVO;
    }

    private MetadataDTO generateVisitsMetadataDTO(String name, List<VisitsCalCount> counts, List<String> list) {
        Map<String, Long> map = new HashMap<>();
        counts.forEach(count -> {
            if (map.containsKey(count.getCalHour())) {
                map.put(count.getCalHour(), map.get(count.getCalHour()) + count.getNum());
            } else {
                map.put(count.getCalHour(), count.getNum());
            }
        });
        return getMetadataDTO(name, list, map.entrySet().stream().map(entry ->
                        new AbstractMap.SimpleEntry<>(
                                entry.getKey(),
                                String.valueOf(entry.getValue())
                        ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private MetadataDTO generateMetadataDTO(String name, List<VisitorCalCount> counts, List<String> list) {
        Map<String, Long> map = new HashMap<>();
        counts.forEach(count -> {
            if (map.containsKey(count.getCalHour())) {
                map.put(count.getCalHour(), map.get(count.getCalHour()) + count.getNum());
            } else {
                map.put(count.getCalHour(), count.getNum());
            }
        });

        return getMetadataDTO(name, list, map.entrySet().stream().map(entry ->
                        new AbstractMap.SimpleEntry<>(
                                entry.getKey(),
                                String.valueOf(entry.getValue())
                        ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private MetadataDTO getMetadataDTO(String name, List<String> list, Map<String, String> map) {
        List<String> values = list.stream().map(a -> {
            String val = map.get(a);
            return StrUtil.isEmpty(val) ? "0" : val;
        }).collect(Collectors.toList());
        MetadataDTO dto = new MetadataDTO();
        dto.setName(name);
        dto.setValues(values);
        return dto;
    }

    @Override
    public LineChartVO behaviorData(String mmCode, Date startTime, Date endTime) {
        mmCode = getMmCode(mmCode);
        LineChartVO lineChartVO = new LineChartVO();

        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);
        List<BehaviorData> behaviorDataList = behaviorDataService.list(mmCode, startTime, endTime);
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<BehaviorData> collect = assemblyManager.getBehaviorData(endTime).stream().filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode)).collect(Collectors.toList());
            behaviorDataList.addAll(collect);
        }
        lineChartVO.getList().add(getMetadataDTO("PV", dateList, behaviorDataList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getPv())))));
        lineChartVO.getList().add(getMetadataDTO("UV", dateList, behaviorDataList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getUv())))));
        lineChartVO.getList().add(getMetadataDTO("MV (Member visitor)", dateList, behaviorDataList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getMv())))));
        lineChartVO.getList().add(getMetadataDTO("new UV", dateList, behaviorDataList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getNewUv())))));
        return lineChartVO;

    }

    /**
     * 功能描述: 计算两者间时间 以yyyy.MM.dd格式返回
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/6 用户行为分析
     */
    private List<String> getDateList(Date startTime, Date endTime) {
        return DateUtil.rangeToList(startTime, endTime, DateField.DAY_OF_YEAR).stream().map(x -> DateUtil.format(x, "yyyy.MM.dd")).collect(Collectors.toList());
    }

    @Override
    public LineChartVO visitorClicksOnProduct(String mmCode, Date startTime, Date endTime) {
        //校验
        mmCode = getMmCode(mmCode);
        LineChartVO lineChartVO = new LineChartVO();

        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);

        List<VisitorClicksOnProduct> list = visitorClicksOnProductService.list(mmCode, startTime, endTime);

        List<VisitorClicksOnProduct> memberList = new ArrayList<>();
        List<VisitorClicksOnProduct> allList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            memberList = list.stream().filter(x -> Objects.equals(x.getMember(), "Member")).collect(Collectors.toList());
            allList = list.stream().filter(x -> Objects.equals(x.getMember(), "All")).collect(Collectors.toList());
        }
        lineChartVO.getList().add(getMetadataDTO("All", dateList, allList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getProductClick())))));
        lineChartVO.getList().add(getMetadataDTO("Member", dateList, memberList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getProductClick())))));

        return lineChartVO;

    }

    @Override
    public LineChartVO averageVisitorVisits(String mmCode, Date startTime, Date endTime) {
        //校验
        mmCode = getMmCode(mmCode);
        LineChartVO lineChartVO = new LineChartVO();
        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);
        List<AverageVisitorVisits> list = averageVisitorVisitsService.list(mmCode, startTime, endTime);


        List<AverageVisitorVisits> appList = new ArrayList<>();
        List<AverageVisitorVisits> h5List = new ArrayList<>();
        List<AverageVisitorVisits> allList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(list)) {
            appList = list.stream().filter(x -> Objects.equals(x.getChannel(), "APP")).collect(Collectors.toList());
            h5List = list.stream().filter(x -> Objects.equals(x.getChannel(), "H5")).collect(Collectors.toList());
            allList = list.stream().filter(x -> Objects.equals(x.getChannel(), "All")).collect(Collectors.toList());
        }

        DecimalFormat format = new DecimalFormat("#.000");
        lineChartVO.getList().add(getMetadataDTO("All", dateList, allList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> format.format(x.getPvDivideMv())))));
        lineChartVO.getList().add(getMetadataDTO("H5", dateList, h5List.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> format.format(x.getPvDivideMv())))));
        lineChartVO.getList().add(getMetadataDTO("APP", dateList, appList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> format.format(x.getPvDivideMv())))));

        return lineChartVO;
    }

    @Override
    public List<BarChartVO> channelVisitorConversion(String mmCode, Date startTime, Date endTime) {
        //校验
        mmCode = getMmCode(mmCode);
        List<BarChartVO> list = channelVisitorConversionService.list(mmCode, startTime, endTime);
        if (CollUtil.isEmpty(list)) {
            return emptyChannelVisitorConversion();
        }
        return list;
    }

    /**
     * 功能描述:返回空数据模板 email/sms/line/facebook/app
     *
     * @Author: 卢嘉俊
     * @Date: 2022/9/2 数据分析
     */
    private List<BarChartVO> emptyChannelVisitorConversion() {
        List<BarChartVO> list = new ArrayList<>();
        list.add(new BarChartVO("email", 0L, 0L));
        list.add(new BarChartVO("sms", 0L, 0L));
        list.add(new BarChartVO("line", 0L, 0L));
        list.add(new BarChartVO("facebook", 0L, 0L));
        list.add(new BarChartVO("app", 0L, 0L));
        return list;
    }

    @Override
    public Collection<BarChartVO> memberTypeClickThroughRate(String mmCode, Date startTime, Date endTime, String ids) {
        //校验
        mmCode = getMmCode(mmCode);

        List<String> memberTypeList = getMemberTypeList(ids);

        List<BarChartVO> barChartVOList = assemblyDataByMemberTypeService.list(mmCode, startTime, endTime, memberTypeList);

        Map<String, BarChartVO> result = new HashMap<>();
        //聚合成大类
        barChartVOList.forEach(x -> {
            switch (CustomerTypeEnum.getNameByMemberType(x.getName())) {
                case "FR":
                    BarChartVO fr = result.getOrDefault("FR", new BarChartVO("FR", 0L, 0L));
                    fr.setValue(x.getValue() + fr.getValue());
                    fr.setTotal(x.getTotal() + fr.getTotal());
                    result.put("FR", fr);
                    break;
                case "NFR":
                    BarChartVO nfr = result.getOrDefault("NFR", new BarChartVO("NFR", 0L, 0L));
                    nfr.setValue(x.getValue() + nfr.getValue());
                    nfr.setTotal(x.getTotal() + nfr.getTotal());
                    result.put("NFR", nfr);
                    break;
                case "HO":
                    BarChartVO ho = result.getOrDefault("HO", new BarChartVO("HO", 0L, 0L));
                    ho.setValue(x.getValue() + ho.getValue());
                    ho.setTotal(x.getTotal() + ho.getTotal());
                    result.put("HO", ho);
                    break;
                case "SV":
                    BarChartVO sv = result.getOrDefault("SV", new BarChartVO("SV", 0L, 0L));
                    sv.setValue(x.getValue() + sv.getValue());
                    sv.setTotal(x.getTotal() + sv.getTotal());
                    result.put("SV", sv);
                    break;
                case "DT":
                    BarChartVO dt = result.getOrDefault("DT", new BarChartVO("DT", 0L, 0L));
                    dt.setValue(x.getValue() + dt.getValue());
                    dt.setTotal(x.getTotal() + dt.getTotal());
                    result.put("DT", dt);
                    break;
                case "OT":
                    BarChartVO ot = result.getOrDefault("OT", new BarChartVO("OT", 0L, 0L));
                    ot.setValue(x.getValue() + ot.getValue());
                    ot.setTotal(x.getTotal() + ot.getTotal());
                    result.put("OT", ot);
                    break;
                default:
                    break;
            }
        });

        return result.values();
    }

    @NotNull
    private String getMmCode(String mmCode) {
        if (StrUtil.isEmpty(mmCode)) {
            mmCode = "0";
        }
        return mmCode;
    }

    @Nullable
    private List<String> getMemberTypeList(String ids) {
        List<String> memberTypeList = null;
        if (!StrUtil.isBlank(ids)) {
            memberTypeList = Arrays.asList(ids.split(","));
        }
        return memberTypeList;
    }

    @Override
    public IPage<ProductAnalysis> productAnalysis(String mmCode, Date startTime, Date endTime, String itemCode, Integer page, Integer limit, String nameEn, String nameThai) {
        Assert.isTrue(DateUtil.betweenDay(startTime, endTime, true) < 93L, StatStatusCode.ONLY_DATA_WITHIN_THREE_MONTHS_SEARCHED);
        MakroPage<ProductAnalysis> result = new MakroPage<>(page, limit);
        //这里list只有itemCode 统计量
        List<ProductAnalysis> goodsCodeList = productAnalysisService.page(result, mmCode, startTime, endTime, itemCode, nameEn, nameThai);
        if (goodsCodeList.isEmpty()) {
            return result;
        }
        List<String> goodsCodes = goodsCodeList.stream().map(ProductAnalysis::getGoodsCode).collect(Collectors.toList());
        List<ProductAnalysis> nameList = productAnalysisService.listForName(mmCode, startTime, endTime, goodsCodes);
        //融合
        List<ProductAnalysis> collect = fuseList(goodsCodeList, nameList);
        //统计
        result.setRecords(collect);
        return result;
    }

    @NotNull
    private List<ProductAnalysis> fuseList(List<ProductAnalysis> goodsCodeList, List<ProductAnalysis> nameList) {
        return nameList.stream().peek(x -> {
            ProductAnalysis productAnalysis = goodsCodeList.stream().filter(goodsCode -> Objects.equals(goodsCode.getGoodsCode(), x.getGoodsCode())).findFirst().orElse(new ProductAnalysis());
            x.setClicks(productAnalysis.getClicks());
            x.setVisitors(productAnalysis.getVisitors());
        }).collect(Collectors.toList());
    }

    @Override
    public LineChartVO channelvisitors(String mmCode, Date startTime, Date endTime) {
        //校验
        mmCode = getMmCode(mmCode);
        LineChartVO lineChartVO = new LineChartVO();

        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);
        List<ChannelvisitorsDTO> list = channelVisitorConversionService.listForChannel(mmCode, startTime, endTime);

        List<ChannelvisitorsDTO> emailList = new ArrayList<>();
        List<ChannelvisitorsDTO> lineList = new ArrayList<>();
        List<ChannelvisitorsDTO> otherList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            emailList = list.stream().filter(x -> Objects.equals(x.getChannel(), "email")).collect(Collectors.toList());
            lineList = list.stream().filter(x -> Objects.equals(x.getChannel(), "line")).collect(Collectors.toList());
            otherList = list.stream().filter(x -> ObjectUtil.notEqual(x.getChannel(), "email") && ObjectUtil.notEqual(x.getChannel(), "line")).collect(Collectors.toList());
        }

        lineChartVO.getList().add(getMetadataDTO("email", dateList, emailList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getUv())))));
        lineChartVO.getList().add(getMetadataDTO("line", dateList, lineList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getUv())))));
        HashMap<String, String> map = new HashMap<>();
        otherList.forEach(x -> {
            String key = DateUtil.format(x.getDate(), "yyyy.MM.dd");
            String value = map.get(key);
            if (ObjectUtil.isNotNull(value)) {
                map.put(key, String.valueOf(Long.parseLong(value) + x.getValue()));
            } else {
                map.put(key, String.valueOf(x.getValue()));
            }
        });
        lineChartVO.getList().add(getMetadataDTO("other", dateList, map));

        return lineChartVO;
    }

    @Override
    public LineChartVO channelVisits(String mmCode, Date startTime, Date endTime) {
        //校验
        mmCode = getMmCode(mmCode);
        LineChartVO lineChartVO = new LineChartVO();

        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);
        List<ChannelvisitorsDTO> list = channelVisitorConversionService.listForChannel(mmCode, startTime, endTime);


        List<ChannelvisitorsDTO> emailList = new ArrayList<>();
        List<ChannelvisitorsDTO> lineList = new ArrayList<>();
        List<ChannelvisitorsDTO> otherList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            emailList = list.stream().filter(x -> Objects.equals(x.getChannel(), "email")).collect(Collectors.toList());
            lineList = list.stream().filter(x -> Objects.equals(x.getChannel(), "line")).collect(Collectors.toList());
            otherList = list.stream().filter(x -> ObjectUtil.notEqual(x.getChannel(), "email") && ObjectUtil.notEqual(x.getChannel(), "line")).collect(Collectors.toList());
        }
        //拆分

        lineChartVO.getList().add(getMetadataDTO("email", dateList, emailList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getValue())))));
        lineChartVO.getList().add(getMetadataDTO("line", dateList, lineList.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getValue())))));
        HashMap<String, String> map = new HashMap<>();
        otherList.forEach(x -> {
            String key = DateUtil.format(x.getDate(), "yyyy.MM.dd");
            String value = map.get(key);
            if (ObjectUtil.isNotNull(value)) {
                map.put(key, String.valueOf(Long.parseLong(value) + x.getValue()));
            } else {
                map.put(key, String.valueOf(x.getValue()));
            }
        });
        lineChartVO.getList().add(getMetadataDTO("other", dateList, map));

        return lineChartVO;
    }

    @Override
    public IPage<VisitorDataVO> visitDetails(VisitorDataDTO visitorDataDTO) {
        Assert.notNull(visitorDataDTO.getPage(), StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(visitorDataDTO.getLimit(), StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(visitorDataDTO.getStartTime(), StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(visitorDataDTO.getEndTime(), StatStatusCode.PARAM_IS_NULL);
        MakroPage<VisitorDataVO> result = new MakroPage<>(visitorDataDTO.getPage(), visitorDataDTO.getLimit());
        try {
            //如果
            if (StrUtil.isNotBlank(visitorDataDTO.getIp()) && !(InetAddress.getByName(visitorDataDTO.getIp()) instanceof Inet4Address)) {
                return result;
            }
        } catch (Exception e) {
            return result;
        }
        List<VisitorDataVO> visitorDataVOS = pageViewLogService.listForVisitDetails(result, visitorDataDTO);
        //反查万客隆会员号
        List<String> cIds = visitorDataVOS.stream().map(VisitorDataVO::getMemberNo).collect(Collectors.toList());
        if (CollUtil.isEmpty(cIds)) {
            result.setRecords(new ArrayList<>());
            return result;
        }
        List<MmCustomer> customer = customerFeignClient.getByIds(cIds).getData();
        visitorDataVOS.forEach(x -> customer.forEach(c -> {
            String id = c.getId().toString();
            if (StrUtil.equals(x.getMemberNo(), id)) {
                x.setMemberNo(id);
            }
        }));

        result.setRecords(visitorDataVOS);
        return result;
    }

    /**
     * 功能描述:商品对比
     *
     * @Return: key visitors 访问人数 key2 visits 访问次数
     * @Author: 卢嘉俊
     * @Date: 2022/7/12 用户行为分析
     */
    @Override
    public Map<String, LineChartVO> compareProduct(Date startTime, Date endTime, String goodsCode, String mmCode, String mmCode2) {
        //校验
        Assert.notNull(startTime, StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(endTime, StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(mmCode, StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(goodsCode, StatStatusCode.PARAM_IS_NULL);
        //装配
        MmActivity mm1 = mmActivityFeignClient.getByCode(mmCode).getData();
        List<ProductAnalysis> list2 = new ArrayList<>();
        MmActivity mm2 = new MmActivity();
        if (StrUtil.isNotBlank(mmCode2)) {
            mm2 = mmActivityFeignClient.getByCode(mmCode2).getData();
            list2 = productAnalysisService.list(mmCode2, goodsCode, startTime, endTime);
        }
        Map<String, LineChartVO> map = new HashMap<>(2);
        List<ProductAnalysis> list1 = productAnalysisService.list(mmCode, goodsCode, startTime, endTime);
        map.put("visitors", this.compareProductVisitors(startTime, endTime, list1, list2, mm1.getTitle(), mm2.getTitle()));
        map.put("visits", this.compareProductVisits(startTime, endTime, list1, list2, mm1.getTitle(), mm2.getTitle()));
        return map;
    }

    private LineChartVO compareProductVisits(Date startTime, Date endTime, List<ProductAnalysis> list1, List<ProductAnalysis> list2, String mm1, String mm2) {
        //校验
        LineChartVO lineChartVO = new LineChartVO();
        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);
        if (CollectionUtil.isEmpty(list1)) {
            lineChartVO.getList().add(getMetadataDTO(mm1, dateList, dateList.stream().collect(Collectors.toMap(key -> key, x -> "0"))));
        } else {
            //组装
            lineChartVO.setUpdateTime(list1.stream().max(Comparator.comparing(ProductAnalysis::getDate)).orElse(new ProductAnalysis()).getDate());
            lineChartVO.getList().add(getMetadataDTO(mm1, dateList, list1.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getClicks())))));
        }

        if (CollectionUtil.isEmpty(list2)) {
            lineChartVO.getList().add(getMetadataDTO(mm2, dateList, dateList.stream().collect(Collectors.toMap(key -> key, x -> "0"))));
        } else {
            lineChartVO.getList().add(getMetadataDTO(mm2, dateList, list2.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getClicks())))));
        }

        return lineChartVO;
    }


    private LineChartVO compareProductVisitors(Date startTime, Date endTime, List<ProductAnalysis> list1, List<ProductAnalysis> list2, String mm1, String mm2) {
        //校验
        LineChartVO lineChartVO = new LineChartVO();

        List<String> dateList = getDateList(startTime, endTime);
        lineChartVO.setLabel(dateList);

        if (CollectionUtil.isEmpty(list1)) {
            lineChartVO.getList().add(getMetadataDTO(mm1, dateList, dateList.stream().collect(Collectors.toMap(key -> key, x -> "0"))));
        } else {
            //组装
            lineChartVO.setUpdateTime(list1.stream().max(Comparator.comparing(ProductAnalysis::getDate)).orElse(new ProductAnalysis()).getDate());
            lineChartVO.getList().add(getMetadataDTO(mm1, dateList, list1.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getVisitors())))));
        }

        if (CollectionUtil.isEmpty(list2)) {
            lineChartVO.getList().add(getMetadataDTO(mm2, dateList, dateList.stream().collect(Collectors.toMap(key -> key, x -> "0"))));
        } else {
            lineChartVO.getList().add(getMetadataDTO(mm2, dateList, list2.stream().collect(Collectors.toMap(key -> DateUtil.format(key.getDate(), "yyyy.MM.dd"), x -> String.valueOf(x.getVisitors())))));
        }

        return lineChartVO;
    }


    @Override
    public LineChartVO mostVisitPage(String mmCode, Date startTime, Date endTime, Boolean desc) {
        mmCode = getMmCode(mmCode);
        String start = DateUtil.format(startTime, "yyyy-MM-dd");
        String end = DateUtil.format(endTime, "yyyy-MM-dd");
        LineChartVO lineChartVO = new LineChartVO();

        QueryWrapper<PagePageNo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("page_no as pageNo,sum(pv) as pv")
                .ne("page_no", "")
                .between("date", start, end)
                .eq("mm_code", mmCode)
                .groupBy("page_no");
        List<PagePageNo> list = pagePageNoService.list(queryWrapper);
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<PagePageNo> collect = assemblyManager.getPagePageNo(endTime).stream().filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode)).collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                list = collect;
            } else {
                list.forEach(x -> x.setPv(x.getPv() + collect.stream().filter(y -> StrUtil.equals(x.getPageNo(), y.getPageNo())).mapToLong(PagePageNo::getPv).sum()));
            }
        }
        if (desc) {
            list = list.stream().sorted(Comparator.comparing(PagePageNo::getPv, Comparator.reverseOrder())).limit(5L).collect(Collectors.toList());
        } else {
            list = list.stream().sorted(Comparator.comparing(PagePageNo::getPv)).limit(5L).collect(Collectors.toList());
        }

        lineChartVO.setLabel(list.stream().map(PagePageNo::getPageNo).collect(Collectors.toList()));

        MetadataDTO dto = new MetadataDTO();
        dto.setName("visit counts");
        dto.setValues(list.stream().map(x -> String.valueOf(x.getPv())).collect(Collectors.toList()));
        lineChartVO.setList(List.of(dto));
        return lineChartVO;
    }

    @Override
    public LineChartVO mostItemClickPage(String mmCode, Date startTime, Date endTime, Boolean desc) {
        String start = DateUtil.format(startTime, "yyyy-MM-dd");
        String end = DateUtil.format(endTime, "yyyy-MM-dd");
        LineChartVO lineChartVO = new LineChartVO();

        QueryWrapper<ProductAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("page_no as pageNo, sum(clicks) as clicks")
                .between("date", start, end)
                .eq(StrUtil.isNotBlank(mmCode), "mm_code", mmCode)
                .groupBy("page_no");
        List<ProductAnalysis> list = productAnalysisService.list(queryWrapper);
        if (DateUtil.isSameDay(endTime, new Date())) {
            List<ProductAnalysis> collect = assemblyManager.getProductAnalyses(endTime).stream().filter(x -> {
                if (StrUtil.isNotBlank(mmCode)) {
                    return StrUtil.equals(x.getMmCode(), mmCode);
                }
                return true;
            }).collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                Map<String, Long> map = collect.stream().collect(Collectors.groupingBy(ProductAnalysis::getPageNo, Collectors.summingLong(ProductAnalysis::getClicks)));
                list = map.keySet().stream().map(x -> {
                    ProductAnalysis productAnalysis = new ProductAnalysis();
                    productAnalysis.setPageNo(x);
                    productAnalysis.setClicks(map.get(x));
                    return productAnalysis;
                }).collect(Collectors.toList());
            } else {
                list.forEach(x -> x.setClicks(x.getClicks() + collect.stream().filter(y -> StrUtil.equals(x.getPageNo(), y.getPageNo())).mapToLong(ProductAnalysis::getClicks).sum()));
            }
        }
        if (desc) {
            list = list.stream().sorted(Comparator.comparing(ProductAnalysis::getClicks, Comparator.reverseOrder())).limit(5L).collect(Collectors.toList());
        } else {
            list = list.stream().sorted(Comparator.comparing(ProductAnalysis::getClicks)).limit(5L).collect(Collectors.toList());
        }

        lineChartVO.setLabel(list.stream().map(ProductAnalysis::getPageNo).collect(Collectors.toList()));

        MetadataDTO dto = new MetadataDTO();
        dto.setName("visit counts");
        dto.setValues(list.stream().map(x -> String.valueOf(x.getClicks())).collect(Collectors.toList()));
        lineChartVO.setList(List.of(dto));
        return lineChartVO;

    }

    @Override
    public List<ChannelPieDTO> channelPie(String mmCode, Date startTime, Date endTime) {
        mmCode = getMmCode(mmCode);
        List<ChannelPieDTO> list = new ArrayList<>();
        double sum = 0D;

        for (String channel : ListUtil.of("email", "sms", "line", "facebook", "app")) {
            Long aLong = channelVisitorConversionService.sumChannelPie(mmCode, startTime, endTime, channel);
            long count = ObjectUtil.isNull(aLong) ? 0 : aLong;
            if (DateUtil.isSameDay(endTime, new Date())) {
                String finalMmCode = mmCode;
                List<ChannelVisitorConversion> channelVisitorConversion = assemblyManager.getChannelVisitorConversion(endTime);
                if (channelVisitorConversion != null) {
                    long count2 = channelVisitorConversion.stream()
                            .filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode))
                            .filter(x -> StrUtil.equals(x.getChannel(), channel))
                            .mapToLong(ChannelVisitorConversion::getValue)
                            .sum();
                    count += count2;
                }
            }
            sum += count;
            ChannelPieDTO channelPieDTO = new ChannelPieDTO();
            channelPieDTO.setName(channel);
            channelPieDTO.setValue(count);
            list.add(channelPieDTO);
        }
        Double finalSum = sum;
        list.forEach(x -> {
            if (ObjectUtil.isNotNull(x.getValue()) && !ObjectUtil.equal(x.getValue(), 0L)) {
                x.setRate(NumberUtil.div(Double.valueOf(x.getValue()), finalSum, 2));
            } else {
                x.setRate(0D);
            }
        });
        return list;

    }

    @Override
    public List<SummaryOfClicksDTO> summaryOfClicks(String mmCode, Date startTime, Date endTime, String channels) {
        mmCode = getMmCode(mmCode);
        String start = DateUtil.format(startTime, "yyyy-MM-dd");
        String end = DateUtil.format(endTime, "yyyy-MM-dd");
        List<String> channelList = Arrays.stream(channels.split(",")).collect(Collectors.toList());
        if (CollUtil.isEmpty(channelList)) {
            channelList.add("");
        }
        List<SummaryOfClicksDTO> result = new ArrayList<>();
        List<PagePageNo> totalView = pagePageNoService.list(new LambdaQueryWrapper<PagePageNo>()
                .select(PagePageNo::getPv)
                .between(PagePageNo::getDate, start, end)
                .eq(PagePageNo::getMmCode, mmCode)
                .in(PagePageNo::getChannel, channelList));
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<PagePageNo> channelVisitorConversion = assemblyManager.getPagePageNo(endTime);
            if (channelVisitorConversion != null) {
                List<PagePageNo> collect = channelVisitorConversion.stream()
                        .filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode))
                        .collect(Collectors.toList());
                totalView.addAll(collect);
            }
        }
        result.add(new SummaryOfClicksDTO("Total view", CollectionUtil.isEmpty(totalView) ? "0" : String.valueOf(totalView.stream().mapToLong(PagePageNo::getPv).sum()), null, ""));

        List<ProductAnalysis> itemClick = productAnalysisService.list(new QueryWrapper<ProductAnalysis>()
                .select("goods_code,page_no as pageNo, sum(clicks) as clicks")
                .between("date", start, end)
                .eq(ObjectUtil.notEqual(mmCode, "0"), "mm_code", mmCode)
                .groupBy("goods_code,page_no")
                .in("channel", channelList));
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<ProductAnalysis> channelVisitorConversion = assemblyManager.getProductAnalyses(endTime).stream().filter(y -> {
                if (ObjectUtil.equal(finalMmCode, "0")) {
                    return true;
                }
                return StrUtil.equals(y.getMmCode(), finalMmCode);
            }).collect(Collectors.toList());
            if (CollUtil.isEmpty(itemClick)) {
                itemClick = channelVisitorConversion;
            } else {
                itemClick.forEach(x -> x.setClicks(x.getClicks() + channelVisitorConversion.stream()
                        .filter(y -> StrUtil.equals(y.getPageNo(), x.getPageNo()) && StrUtil.equals(y.getGoodsCode(), x.getGoodsCode()))
                        .mapToLong(ProductAnalysis::getClicks)
                        .sum()));
            }
        }
        itemClick = itemClick.stream().sorted(Comparator.comparing(ProductAnalysis::getClicks, Comparator.reverseOrder())).collect(Collectors.toList());
        List<String> collect = new ArrayList<>();
        Long clicks = null;
        for (ProductAnalysis x : itemClick) {
            if (ObjectUtil.isNull(clicks)) {
                clicks = x.getClicks();
            }
            if (ObjectUtil.equal(clicks, x.getClicks())) {
                collect.add(x.getGoodsCode());
            }
        }
        result.add(new SummaryOfClicksDTO("Most Item Click", String.valueOf(clicks), collect, CollUtil.isNotEmpty(collect) ? StrUtil.join("/", collect) : ""));

        itemClick = itemClick.stream().sorted(Comparator.comparing(ProductAnalysis::getClicks)).collect(Collectors.toList());
        Long clicks2 = null;
        List<String> collect2 = new ArrayList<>();
        for (ProductAnalysis x : itemClick) {
            if (ObjectUtil.isNull(clicks2)) {
                clicks2 = x.getClicks();
            }
            if (ObjectUtil.equal(clicks2, x.getClicks())) {
                collect2.add(x.getGoodsCode());
            }
        }
        result.add(new SummaryOfClicksDTO("least Item Click", String.valueOf(clicks2), collect2, CollUtil.isNotEmpty(collect2) ? StrUtil.join("/", collect2) : ""));


        List<PagePageNo> pageClick = pagePageNoService.list(new QueryWrapper<PagePageNo>()
                .select("page_no as pageNo, sum(count) as count")
                .between("date", start, end)
                .eq("mm_code", mmCode)
                .groupBy("page_no")
                .in("channel", channelList));
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<PagePageNo> pagePageNos = assemblyManager.getPagePageNo(endTime).stream()
                    .filter(y -> StrUtil.equals(y.getMmCode(), finalMmCode))
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(pageClick)) {
                pageClick = pagePageNos;
            } else {
                pageClick.forEach(x -> x.setCount(x.getCount() + pagePageNos.stream()
                        .filter(y -> StrUtil.equals(y.getPageNo(), x.getPageNo()))
                        .mapToLong(PagePageNo::getCount)
                        .sum()));
            }
        }
        pageClick = pageClick.stream().sorted(Comparator.comparing(PagePageNo::getCount, Comparator.reverseOrder())).collect(Collectors.toList());
        Long clicks3 = null;
        List<String> collect3 = new ArrayList<>();
        for (PagePageNo x : pageClick) {
            if (ObjectUtil.isNull(clicks3)) {
                clicks3 = x.getCount();
            }
            if (ObjectUtil.equal(clicks3, x.getCount())) {
                collect3.add("Page" + x.getPageNo());
            }
        }
        result.add(new SummaryOfClicksDTO("Most Page Click", String.valueOf(clicks3), collect3, CollUtil.isNotEmpty(collect3) ? StrUtil.join("/", collect3) : ""));


        pageClick = pageClick.stream().sorted(Comparator.comparing(PagePageNo::getCount)).collect(Collectors.toList());
        Long clicks4 = null;
        List<String> collect4 = new ArrayList<>();
        for (PagePageNo x : pageClick) {
            if (ObjectUtil.isNull(clicks4)) {
                clicks4 = x.getCount();
            }
            if (ObjectUtil.equal(clicks4, x.getCount())) {
                collect4.add("Page" + x.getPageNo());
            }
        }
        result.add(new SummaryOfClicksDTO("least Page Click", String.valueOf(clicks4), collect4, CollUtil.isNotEmpty(collect4) ? StrUtil.join("/", collect4) : ""));

        List<ProductAnalysis> totalClicks = productAnalysisService.list(new QueryWrapper<ProductAnalysis>()
                .select("sum(clicks) as clicks")
                .between("date", start, end)
                .eq(ObjectUtil.notEqual(mmCode, "0"), "mm_code", mmCode)
                .in("channel", channelList));
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<ProductAnalysis> productAnalyses = assemblyManager.getProductAnalyses(endTime).stream()
                    .filter(y -> {
                        if (ObjectUtil.equal(finalMmCode, "0")) {
                            return true;
                        } else {
                            return StrUtil.equals(y.getMmCode(), finalMmCode);
                        }
                    }).collect(Collectors.toList());
            totalClicks.addAll(productAnalyses);
        }
        result.add(new SummaryOfClicksDTO("Click to Makro Click", String.valueOf(totalClicks.stream().mapToLong(ProductAnalysis::getClicks).sum()), null, ""));
        return result;
    }

    @Override
    public List<SummaryOfClicksDetailVO> summaryOfClicksDetail(String mmCode, Date startTime, Date endTime, String channels) {
        mmCode = getMmCode(mmCode);
        String start = DateUtil.format(startTime, "yyyy-MM-dd");
        String end = DateUtil.format(endTime, "yyyy-MM-dd");
        List<SummaryOfClicksDetailVO> result = new ArrayList<>();

        List<ProductAnalysis> totalView = productAnalysisService.list(new QueryWrapper<ProductAnalysis>()
                        .select("sum(clicks) as clicks,page_no")
                        .between("date", start, end)
                        .eq("mm_code", mmCode)
                        .groupBy("page_no")
                        .in(StrUtil.isNotBlank(channels), "channel", Arrays.stream(channels.split(",")).collect(Collectors.toList()))
                        .eq(StrUtil.isBlank(channels), "channel", ""))
                .stream()
                .filter(x -> ObjectUtil.notEqual(x.getPageNo(), ""))
                .collect(Collectors.toList());

        long maxPageNo = totalView.stream().mapToLong(x -> Long.parseLong(x.getPageNo())).max().orElse(0);

        Set<Integer> pageNoSet = totalView.stream().map(x -> Integer.parseInt(x.getPageNo())).collect(Collectors.toSet());

        for (int i = 1; i <= maxPageNo; i++) {
            SummaryOfClicksDetailVO vo = new SummaryOfClicksDetailVO();
            vo.setPageNo(i);

            if (!pageNoSet.contains(i)) {
                vo.setTotalView(0L);
                vo.setTotalClicks(0L);
            } else {
                PagePageNo count = pagePageNoService.getOne(new QueryWrapper<PagePageNo>()
                        .select("sum(count) as count")
                        .between("date", start, end)
                        .eq(ObjectUtil.notEqual(mmCode, "0"), "mm_code", mmCode)
                        .eq("page_no", String.valueOf(i))
                        .in(StrUtil.isNotBlank(channels), "channel", Arrays.stream(channels.split(",")).collect(Collectors.toList()))
                        .in(StrUtil.isBlank(channels), "channel", ""));
                vo.setTotalView(count.getCount());
                List<ProductAnalysis> mostItemClick = productAnalysisService.list(new QueryWrapper<ProductAnalysis>()
                        .select("sum(clicks) as clicks,goods_code")
                        .between("date", start, end)
                        .eq("mm_code", mmCode)
                        .eq("page_no", i)
                        .orderByDesc("clicks")
                        .groupBy("goods_code")
                        .in(StrUtil.isNotBlank(channels), "channel", Arrays.stream(channels.split(",")).collect(Collectors.toList()))
                        .eq(StrUtil.isBlank(channels), "channel", "")
                        .last("limit 100"));
                Long clicks = null;
                List<String> collect = new ArrayList<>();
                for (ProductAnalysis x : mostItemClick) {
                    if (ObjectUtil.isNull(clicks)) {
                        clicks = x.getClicks();
                    }
                    if (ObjectUtil.equal(clicks, x.getClicks())) {
                        collect.add(x.getGoodsCode());
                    }
                }
                vo.setMostItemClick(String.valueOf(clicks));
                vo.setMostItem(collect);
                List<ProductAnalysis> leastItemClick = productAnalysisService.list(new QueryWrapper<ProductAnalysis>()
                        .select("sum(clicks) as clicks,goods_code")
                        .between("date", start, end)
                        .eq("mm_code", mmCode)
                        .eq("page_no", i)
                        .orderByAsc("clicks")
                        .groupBy("goods_code")
                        .in(StrUtil.isNotBlank(channels), "channel", Arrays.stream(channels.split(",")).collect(Collectors.toList()))
                        .eq(StrUtil.isBlank(channels), "channel", "")
                        .last("limit 100"));
                Long clicks2 = null;
                List<String> collect2 = new ArrayList<>();
                for (ProductAnalysis x : leastItemClick) {
                    if (ObjectUtil.isNull(clicks2)) {
                        clicks2 = x.getClicks();
                    }
                    if (ObjectUtil.equal(clicks2, x.getClicks())) {
                        collect2.add(x.getGoodsCode());
                    }
                }
                vo.setLeastItemClick(String.valueOf(clicks2));
                vo.setLeastItem(collect2);
                List<ProductAnalysis> totalClicks = productAnalysisService.list(new LambdaQueryWrapper<ProductAnalysis>()
                        .eq(ProductAnalysis::getPageNo, i)
                        .select(ProductAnalysis::getClicks)
                        .between(ProductAnalysis::getDate, start, end)
                        .eq(ProductAnalysis::getMmCode, mmCode)
                        .in(StrUtil.isNotBlank(channels), ProductAnalysis::getChannel, Arrays.stream(channels.split(",")).collect(Collectors.toList()))
                        .in(StrUtil.isBlank(channels), ProductAnalysis::getChannel, ""));
                vo.setTotalClicks(CollectionUtil.isEmpty(totalClicks) ? 0 : totalClicks.stream().mapToLong(ProductAnalysis::getClicks).sum());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public LineChartVO friends(String mmCode, Date startTime, Date endTime, String channels) {
        mmCode = getMmCode(mmCode);
        String start = DateUtil.format(startTime, "yyyy-MM-dd");
        String end = DateUtil.format(endTime, "yyyy-MM-dd");
        LineChartVO lineChartVO = new LineChartVO();

        List<String> customerTypes = Arrays.stream(CustomerTypeEnum.values()).map(CustomerTypeEnum::getName).collect(Collectors.toList());
        lineChartVO.setLabel(Arrays.stream(CustomerTypeEnum.values()).map(CustomerTypeEnum::getName).collect(Collectors.toList()));

        MetadataDTO dto = new MetadataDTO();
        dto.setName("Friends");
        List<PageChannelMemberType> all = pageChannelMemberTypeService.list(new LambdaQueryWrapper<PageChannelMemberType>()
                .between(PageChannelMemberType::getDate, start, end)
                .eq(PageChannelMemberType::getMmCode, mmCode)
                .in(PageChannelMemberType::getChannel, Arrays.stream(channels.split(",")).collect(Collectors.toList())));
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<PageChannelMemberType> pageChannelMemberType = assemblyManager.getPageChannelMemberType(endTime).stream().filter(y -> StrUtil.equals(y.getMmCode(), finalMmCode)).collect(Collectors.toList());
            if (all.isEmpty()) {
                all = pageChannelMemberType;
            } else {
                all.addAll(pageChannelMemberType);
            }
        }
        ArrayList<String> result = new ArrayList<>();
        for (String customerType : customerTypes) {
            long sum = 0;
            switch (customerType) {
                case "FR":
                    sum = all.stream().filter(PageChannelMemberType::isFr).mapToLong(PageChannelMemberType::getUv).sum();
                    break;
                case "NFR":
                    sum = all.stream().filter(PageChannelMemberType::isNfr).mapToLong(PageChannelMemberType::getUv).sum();
                    break;
                case "HO":
                    sum = all.stream().filter(PageChannelMemberType::isHo).mapToLong(PageChannelMemberType::getUv).sum();
                    break;
                case "SV":
                    sum = all.stream().filter(PageChannelMemberType::isSv).mapToLong(PageChannelMemberType::getUv).sum();
                    break;
                case "DT":
                    sum = all.stream().filter(PageChannelMemberType::isDt).mapToLong(PageChannelMemberType::getUv).sum();
                    break;
                case "OT":
                    sum = all.stream().filter(PageChannelMemberType::isOt).mapToLong(PageChannelMemberType::getUv).sum();
                    break;
                default:
                    break;
            }
            result.add(String.valueOf(sum));
        }
        dto.setValues(result);
        lineChartVO.setList(List.of(dto));

        return lineChartVO;
    }

    @Override
    public LineChartVO pageStay(String mmCode, Date startTime, Date endTime, List<String> customerTypes, List<String> channels) {
        Assert.isTrue(StrUtil.isNotEmpty(mmCode), StatStatusCode.PARAM_IS_NULL);
        LineChartVO lineChartVO = new LineChartVO();
        MetadataDTO dto = new MetadataDTO();
        dto.setName("average stay time");
        QueryWrapper<PageStaySummary> queryWrapper = new QueryWrapper<PageStaySummary>()
                .between("cal_date", DateUtil.format(startTime, "yyyy-MM-dd"), DateUtil.format(endTime, "yyyy-MM-dd"))
                .eq("mm_code", mmCode)
                .in(CollectionUtil.isNotEmpty(channels), "channel", channels)
                .and(CollectionUtil.isNotEmpty(customerTypes), wrapper -> {
                    wrapper.or(customerTypes.contains("FR"), wp -> wp.eq("fr", true));
                    wrapper.or(customerTypes.contains("NFR"), wp -> wp.eq("nfr", true));
                    wrapper.or(customerTypes.contains("HO"), wp -> wp.eq("ho", true));
                    wrapper.or(customerTypes.contains("SV"), wp -> wp.eq("sv", true));
                    wrapper.or(customerTypes.contains("DT"), wp -> wp.eq("dt", true));
                    wrapper.or(customerTypes.contains("OT"), wp -> wp.eq("ot", true));
                });
        List<PageStaySummary> list = pageStaySummaryService.list(queryWrapper);

        Date now = new Date();
        if (DateUtil.isSameDay(now, endTime)) {
            // 获取当前这个小时的统计时间，累加到list中
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date currentBegin = calendar.getTime();
            List<PageStayLog> stayLogs = pageStayLogService.list(new LambdaQueryWrapper<PageStayLog>()
                            .between(PageStayLog::getTs, DateUtil.format(currentBegin, "yyyy-MM-dd HH:mm:ss"),
                                    DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))).stream()
                    .filter(x -> StrUtil.isNotBlank(x.getMemberType()) && StrUtil.isNotBlank(x.getChannel())).collect(Collectors.toList());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            String calHour = (hour < 10 ? "0" + hour : hour) + ":00";
            Map<String, PageStaySummary> map = assemblyService.getPageStaySummaryMap(calendar.getTime(),
                    calHour, stayLogs);
            // map转list
            list.addAll(new ArrayList<>(map.values()));
        }

        Map<String, PageStaySummary> summaryMap = new TreeMap<>();
        list.forEach(a -> {
            PageStaySummary pageStaySummary = summaryMap.get(a.getPageNo());
            if (pageStaySummary == null) {
                pageStaySummary = PageStaySummary.builder()
                        .pageNo(a.getPageNo()).visits(0L).visitors(0L).stayTime(0L).newVisitors(0L).bounceRateCounts(0L)
                        .build();
                summaryMap.put(a.getPageNo(), pageStaySummary);
            }
            pageStaySummary.setVisits(pageStaySummary.getVisits() + a.getVisits());
            pageStaySummary.setStayTime(pageStaySummary.getStayTime() + a.getStayTime());
            pageStaySummary.setBounceRateCounts(pageStaySummary.getBounceRateCounts() + a.getBounceRateCounts());
        });

        lineChartVO.setLabel(new ArrayList<>());
        List<String> values = new ArrayList<>();
        List<String> values2 = new ArrayList<>();
        summaryMap.values().forEach(a -> {
            lineChartVO.getLabel().add("Page" + a.getPageNo());
            Long num = a.getVisits() * 1000;
            values.add(String.valueOf(NumberUtil.div(Double.valueOf(a.getStayTime()), num, 2)));
            values2.add(String.valueOf(NumberUtil.div(Double.valueOf(a.getVisits() - a.getBounceRateCounts()), a.getVisits(), 2)));
        });
        dto.setValues(values);
        dto.setValues2(values2);
        lineChartVO.setList(List.of(dto));
        return lineChartVO;
    }

    @Override
    public IPage<StayPageNo> stayPageNo(Integer page, Integer limit, String mmCode, Date startTime, Date endTime, Boolean desc) {
        Assert.notNull(page, StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(limit, StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(startTime, StatStatusCode.PARAM_IS_NULL);
        Assert.notNull(endTime, StatStatusCode.PARAM_IS_NULL);
        MakroPage<StayPageNo> result = new MakroPage<>(page, limit);

        return stayPageNoService.page(result, new QueryWrapper<StayPageNo>()
                .between("date", DateUtil.format(startTime, "yyyy-MM-dd"), DateUtil.format(endTime, "yyyy-MM-dd"))
                .eq(mmCode != null, "mm_code", mmCode)
                .orderBy(desc != null, Boolean.FALSE.equals(desc), "stay_time"));
    }

    @Override
    public List<CustomerTypePieDTO> customerTypePie(String mmCode, Date startTime, Date endTime) {
        mmCode = getMmCode(mmCode);
        ArrayList<CustomerTypePieDTO> list = new ArrayList<>();
        double sum = 0D;

        for (String name : EnumUtil.getNames(CustomerTypeEnum.class)) {
            String noByName = CustomerTypeEnum.getNoByName(name);
            Long aLong = assemblyDataByMemberTypeService.sumCustomerTypePie(mmCode, startTime, endTime, noByName);
            long count = ObjectUtil.isNull(aLong) ? 0 : aLong;

            if (DateUtil.isSameDay(endTime, new Date())) {
                String finalMmCode = mmCode;
                List<AssemblyDataByMemberType> assemblyDataByMemberType = assemblyManager.getAssemblyDataByMemberType(endTime);
                if (assemblyDataByMemberType != null) {
                    long count2 = assemblyDataByMemberType.stream()
                            .filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode))
                            .filter(x -> StrUtil.startWith(x.getMemberType(), noByName))
                            .mapToLong(AssemblyDataByMemberType::getPv)
                            .sum();
                    count += count2;
                }
            }
            sum += count;
            CustomerTypePieDTO customerTypePieDTO = new CustomerTypePieDTO();
            customerTypePieDTO.setName(name);
            customerTypePieDTO.setValue(count);
            list.add(customerTypePieDTO);
        }
        Double finalSum = sum;
        list.forEach(x -> {
            if (ObjectUtil.isNotNull(x.getValue()) && !ObjectUtil.equal(x.getValue(), 0L)) {
                x.setRate(NumberUtil.div(Double.valueOf(x.getValue()), finalSum, 2));
            } else {
                x.setRate(0D);
            }
        });
        return list;
    }

    @Override
    public IPage<PageStayDTO> pageStaySummary(PageStaySummaryRequest request) {
        QueryWrapper<PageStaySummary> queryWrapper = new QueryWrapper<PageStaySummary>()
                .select("page_no as pageNo, sum(visits) as visits, sum(visitors) as visitors, sum(new_visitors) as newVisitors," +
                        "sum(stay_time) as stayTime, sum(bounce_rate_counts) as bounceRateCounts")
                .between("cal_date", DateUtil.format(request.getStartTime(), "yyyy-MM-dd"),
                        DateUtil.format(request.getEndTime(), "yyyy-MM-dd"))
                .eq("mm_code", request.getMmCode())
                .in(CollectionUtil.isNotEmpty(request.getChannels()), "channel", request.getChannels())
                .and(CollectionUtil.isNotEmpty(request.getCustomerTypes()), wrapper -> {
                    wrapper.or(request.getCustomerTypes().contains("FR"), wp -> wp.eq("fr", true));
                    wrapper.or(request.getCustomerTypes().contains("NFR"), wp -> wp.eq("nfr", true));
                    wrapper.or(request.getCustomerTypes().contains("HO"), wp -> wp.eq("ho", true));
                    wrapper.or(request.getCustomerTypes().contains("SV"), wp -> wp.eq("sv", true));
                    wrapper.or(request.getCustomerTypes().contains("DT"), wp -> wp.eq("dt", true));
                    wrapper.or(request.getCustomerTypes().contains("OT"), wp -> wp.eq("ot", true));
                }).groupBy("pageNo")
                .orderByDesc("stayTime");
        MakroPage<PageStaySummary> result = new MakroPage<>(request.getPage(), request.getLimit());
        IPage<PageStaySummary> summary = pageStaySummaryService.page(result, queryWrapper);
        return summary.convert(s -> {
            PageStayDTO dto = new PageStayDTO();
            BeanUtil.copyProperties(s, dto);
            BigDecimal visits = new BigDecimal(dto.getVisits());
            dto.setAverageStayTime(new BigDecimal(dto.getStayTime()).divide(visits, 2, RoundingMode.HALF_UP));
            dto.setBounceRate(new BigDecimal(dto.getBounceRateCounts()).divide(visits, 4, RoundingMode.HALF_UP));
            return dto;
        });
    }

    @Override
    public LineChartVO mixedPanelSummary(MixedPanelSummaryReqDTO dto) {
        if (StrUtil.equals(dto.getStep1(), "page")) {
            if (CollUtil.isEmpty(dto.getPageNos())) {
                Integer total = templateFeignClient.getByMmCode(dto.getMmCode()).getData().getTemplatePageTotal();
                List<String> pageNos = new ArrayList<>();
                for (Integer i = 1; i <= total; i++) {
                    pageNos.add(String.valueOf(i));
                }
                dto.setPageNos(pageNos);
            }
            return mixedPanelSummaryPage(dto);
        } else {
            if (CollUtil.isEmpty(dto.getChannels())) {
                dto.setChannels(List.of("email", "sms", "line", "facebook", "app"));
            }
            if (CollUtil.isEmpty(dto.getMobile())) {
                dto.setMobile(List.of("1", "0"));
            }
            if (CollUtil.isEmpty(dto.getMemberTypes())) {
                dto.setMemberTypes(List.of("FR", "NFR", "HO", "SV", "DT", "OT"));
            }
            return mixedPanelSummarySession(dto);
        }

    }

    private LineChartVO mixedPanelSummarySession(MixedPanelSummaryReqDTO dto) {
        LineChartVO lineChartVO = new LineChartVO();
        lineChartVO.setLabel(dto.getStep());
        ArrayList<MetadataDTO> list2 = new ArrayList<>();
        List<String> values;

        QueryWrapper<MixedPanelSummary> wrapper = new QueryWrapper<MixedPanelSummary>()
                .eq(StrUtil.isNotBlank(dto.getMmCode()), "mm_code", dto.getMmCode())
                .between("date", DateUtil.format(dto.getStartTime(), "yyyy-MM-dd"),
                        DateUtil.format(dto.getEndTime(), "yyyy-MM-dd"))
                .in(CollUtil.isNotEmpty(dto.getChannels()), "channel", dto.getChannels())
                .in(CollUtil.isNotEmpty(dto.getMobile()), "mobile", dto.getMobile())
                .and(CollectionUtil.isNotEmpty(dto.getMemberTypes()), x -> {
                    x.or(dto.getMemberTypes().contains("FR"), wp -> wp.eq("fr", true));
                    x.or(dto.getMemberTypes().contains("NFR"), wp -> wp.eq("nfr", true));
                    x.or(dto.getMemberTypes().contains("HO"), wp -> wp.eq("ho", true));
                    x.or(dto.getMemberTypes().contains("SV"), wp -> wp.eq("sv", true));
                    x.or(dto.getMemberTypes().contains("DT"), wp -> wp.eq("dt", true));
                    x.or(dto.getMemberTypes().contains("OT"), wp -> wp.eq("ot", true));
                });
        StringBuilder sb = new StringBuilder("sum(to_page) as toPage , ");
        if (CollUtil.isEmpty(dto.getStep())) {
            return lineChartVO;
        }
        dto.getStep().forEach(x -> {
            switch (x) {
                case "Access to MM":
                    if (StrUtil.equals(dto.getStep1(), "session")) {
                        sb.append("sum(session_to_mm) as sessionToMm , ");
                    } else {
                        sb.append("sum(member_to_mm) as memberToMm , ");
                    }
                    break;
                case "Access to Item":
                    sb.append("sum(to_item) as toItem , ");
                    break;
                default:
                    break;
            }
        });
        wrapper.select(sb.substring(0, sb.toString().length() - 3));
        MixedPanelSummary one = mixedPanelSummaryService.getOne(wrapper);

        QueryWrapper<PageTotalSuccess> wrapper2 = new QueryWrapper<PageTotalSuccess>()
                .select("sum(total) as total,sum(success) as success")
                .eq(StrUtil.isNotBlank(dto.getMmCode()), "mm_code", dto.getMmCode())
                .between("date", DateUtil.format(dto.getStartTime(), "yyyy-MM-dd"),
                        DateUtil.format(dto.getEndTime(), "yyyy-MM-dd"))
                .in(CollUtil.isNotEmpty(dto.getChannels()), "channel", dto.getChannels())
                .and(CollectionUtil.isNotEmpty(dto.getMemberTypes()), x -> {
                    x.or(dto.getMemberTypes().contains("FR"), wp -> wp.eq("fr", true));
                    x.or(dto.getMemberTypes().contains("NFR"), wp -> wp.eq("nfr", true));
                    x.or(dto.getMemberTypes().contains("HO"), wp -> wp.eq("ho", true));
                    x.or(dto.getMemberTypes().contains("SV"), wp -> wp.eq("sv", true));
                    x.or(dto.getMemberTypes().contains("DT"), wp -> wp.eq("dt", true));
                    x.or(dto.getMemberTypes().contains("OT"), wp -> wp.eq("ot", true));
                });
        PageTotalSuccess pageTotalSuccess = pageTotalSuccessService.getOne(wrapper2);
        if (ObjectUtil.isNotNull(one)) {

            values = new ArrayList<>();
            dto.getStep().forEach(x -> {
                switch (x) {
                    case "Send Total":
                        values.add(ObjectUtil.isNull(pageTotalSuccess) ? "0" : String.valueOf(pageTotalSuccess.getTotal()));
                        break;
                    case "Send Success":
                        values.add(ObjectUtil.isNull(pageTotalSuccess) ? "0" : String.valueOf(pageTotalSuccess.getSuccess()));
                        break;
                    case "Access to MM":
                        if (StrUtil.equals(dto.getStep1(), "session")) {
                            values.add(String.valueOf(one.getSessionToMm()));
                        } else {
                            values.add(String.valueOf(one.getMemberToMm()));
                        }
                        break;
                    case "Access to Page":
                        values.add(String.valueOf(one.getToPage()));
                        break;
                    case "Access to Item":
                        values.add(String.valueOf(one.getToItem()));
                        break;
                    default:
                        break;
                }
            });
        } else {
            List<String> y_axis = new ArrayList<>();
            dto.getStep().forEach(x -> y_axis.add("0"));
            values = y_axis;
        }

        list2.add(new MetadataDTO(dto.getStep1(), values));
        lineChartVO.setList(list2);
        return lineChartVO;
    }

    private LineChartVO mixedPanelSummaryPage(MixedPanelSummaryReqDTO dto) {
        LineChartVO lineChartVO = new LineChartVO();
        ArrayList<String> label = new ArrayList<>();
        dto.getPageNos().forEach(page -> label.add("Page " + page));
        lineChartVO.setLabel(label);


        QueryWrapper<MixedPanelSummary> wrapper = new QueryWrapper<MixedPanelSummary>()
                .eq(StrUtil.isNotBlank(dto.getMmCode()), "mm_code", dto.getMmCode())
                .between("date", DateUtil.format(dto.getStartTime(), "yyyy-MM-dd"),
                        DateUtil.format(dto.getEndTime(), "yyyy-MM-dd"))
                .in(CollUtil.isNotEmpty(dto.getChannels()), "channel", dto.getChannels())
                .in(CollUtil.isNotEmpty(dto.getPageNos()), "page_no", dto.getPageNos())
                .in(CollUtil.isNotEmpty(dto.getMobile()), "mobile", dto.getMobile())
                .and(CollectionUtil.isNotEmpty(dto.getMemberTypes()), x -> {
                    x.or(dto.getMemberTypes().contains("FR"), wp -> wp.eq("fr", true));
                    x.or(dto.getMemberTypes().contains("NFR"), wp -> wp.eq("nfr", true));
                    x.or(dto.getMemberTypes().contains("HO"), wp -> wp.eq("ho", true));
                    x.or(dto.getMemberTypes().contains("SV"), wp -> wp.eq("sv", true));
                    x.or(dto.getMemberTypes().contains("DT"), wp -> wp.eq("dt", true));
                    x.or(dto.getMemberTypes().contains("OT"), wp -> wp.eq("ot", true));
                })
                .orderByAsc("page_no")
                .groupBy("page_no");
        StringBuilder sb = new StringBuilder("page_no as pageNo , sum(session_to_mm_page) as sessionToMmPage , ");
        dto.getDataType().forEach(x -> {
            switch (x) {
                case "Item Click":
                    sb.append("sum(to_item) as toItem , ");
                    break;
                case "Page Stay Time":
                    sb.append("sum(stay_time) as stayTime , ");
                    break;
                case "Bounce Rate":
                    sb.append("sum(bounce_rate_counts) as bounceRateCounts , ");
                    break;
                default:
                    break;
            }
        });
        wrapper.select(sb.substring(0, sb.toString().length() - 3));

        List<MixedPanelSummary> list = mixedPanelSummaryService.list(wrapper);

        ArrayList<MetadataDTO> list2 = new ArrayList<>();
        dto.getDataType().forEach(dateType -> {
            List<String> values = new ArrayList<>();
            MetadataDTO metadataDTO = new MetadataDTO();
            switch (dateType) {
                case "Item Click":
                    metadataDTO.setName("Item Click");
                    break;
                case "Click":
                    metadataDTO.setName("Click");
                    break;
                case "Page Stay Time":
                    metadataDTO.setName("Page Stay Time");
                    metadataDTO.setUnit("s");
                    break;
                case "Bounce Rate":
                    metadataDTO.setName("Bounce Rate");
                    metadataDTO.setUnit("%");
                    break;
                default:
                    break;
            }
            dto.getPageNos().forEach(page -> {
                        AtomicReference<String> pageValue = new AtomicReference<>("0");
                        list.forEach(x -> {
                            if (StrUtil.equals(page, x.getPageNo())) {
                                switch (dateType) {
                                    case "Item Click":
                                        pageValue.set(String.valueOf(x.getToItem()));
                                        break;
                                    case "Click":
                                        pageValue.set(String.valueOf(x.getSessionToMmPage()));
                                        break;
                                    case "Page Stay Time":
                                        if (ObjectUtil.equal(x.getStayTime(), 0L)) {
                                            pageValue.set("0");
                                            break;
                                        }
                                        Double aDouble = Double.valueOf(x.getStayTime());
                                        pageValue.set(String.valueOf(NumberUtil.div(aDouble, Double.valueOf(1000D), 2)));
                                        break;
                                    case "Bounce Rate":
                                        long l = (x.getSessionToMmPage() - x.getBounceRateCounts()) * 100;
                                        if (ObjectUtil.equal(x.getSessionToMmPage(), 0L) || l <= 0L) {
                                            pageValue.set("0");
                                            break;
                                        }
                                        BigDecimal resultBd = NumberUtil.div(Long.valueOf(l), x.getSessionToMmPage(), 2);
                                        pageValue.set(String.valueOf(resultBd));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        values.add(pageValue.get());
                    }
            );
            metadataDTO.setValues(values);
            list2.add(metadataDTO);
        });
        lineChartVO.setList(list2);
        return lineChartVO;
    }


    @Override
    public JSONArray mixedPanelSummaryJson(String mmCode) {
        JSONArray jsonObject = JSON.parseArray("[{\n" +
                "        \"name\": \"Page\",\n" +
                "        \"type\": \"multiple-group\",\n" +
                "        \"value\": \"page\",\n" +
                "        \"checked\": true,\n" +
                "        \"children\": [{\n" +
                "            \"name\": \"Page\",\n" +
                "            \"field\": \"pageNos\",\n" +
                "            \"type\": \"multiple-select\",\n" +
                "            \"children\": [{\n" +
                "                \"name\": \"Page 1\",\n" +
                "                \"value\": \"1\"\n" +
                "            }]\n" +
                "        }, {\n" +
                "            \"name\": \"Data indicators\",\n" +
                "            \"field\": \"dataType\",\n" +
                "            \"type\": \"multiple-select\",\n" +
                "            \"children\": [{\n" +
                "                \"name\": \"Click\",\n" +
                "                \"value\": \"Click\",\n" +
                "                \"checked\": true\n" +
                "            }, {\n" +
                "                \"name\": \"Item Click\",\n" +
                "                \"value\": \"Item Click\",\n" +
                "                \"action\": \"viewItemClicks\"\n" +
                "            }, {\n" +
                "                \"name\": \"Page Stay Time\",\n" +
                "                \"value\": \"Page Stay Time\"\n" +
                "            }, {\n" +
                "                \"name\": \"Bounce Rate\",\n" +
                "                \"value\": \"Bounce Rate\"\n" +
                "            }]\n" +
                "        }]\n" +
                "    }, {\n" +
                "        \"name\": \"Member\",\n" +
                "        \"type\": \"multiple-group\",\n" +
                "        \"value\": \"member\",\n" +
                "        \"children\": [{\n" +
                "            \"name\": \"Step\",\n" +
                "            \"field\": \"step\",\n" +
                "            \"type\": \"multiple-select\",\n" +
                "            \"children\": [{\n" +
                "                \"name\": \"Send Total\",\n" +
                "                \"value\": \"Send Total\"\n" +
                "            }, {\n" +
                "                \"name\": \"Send Success\",\n" +
                "                \"value\": \"Send Success\"\n" +
                "            }, {\n" +
                "                \"name\": \"Access to MM\",\n" +
                "                \"value\": \"Access to MM\"\n" +
                "            }, {\n" +
                "                \"name\": \"Access to Page\",\n" +
                "                \"value\": \"Access to Page\"\n" +
                "            }, {\n" +
                "                \"name\": \"Access to Item\",\n" +
                "                \"value\": \"Access to Item\"\n" +
                "            }]\n" +
                "        }, {\n" +
                "            \"name\": \"Filter\",\n" +
                "            \"type\": \"filter\",\n" +
                "            \"children\": [{\n" +
                "                \"name\": \"Member Type\",\n" +
                "                \"field\": \"memberTypes\",\n" +
                "                \"type\": \"multiple-select\",\n" +
                "                \"children\": [{\n" +
                "                    \"name\": \"FR\",\n" +
                "                    \"value\": \"FR\"\n" +
                "                }, {\n" +
                "                    \"name\": \"NFR\",\n" +
                "                    \"value\": \"NFR\"\n" +
                "                }, {\n" +
                "                    \"name\": \"HO\",\n" +
                "                    \"value\": \"HO\"\n" +
                "                }, {\n" +
                "                    \"name\": \"SV\",\n" +
                "                    \"value\": \"SV\"\n" +
                "                }, {\n" +
                "                    \"name\": \"DT\",\n" +
                "                    \"value\": \"DT\"\n" +
                "                }, {\n" +
                "                    \"name\": \"OT\",\n" +
                "                    \"value\": \"OT\"\n" +
                "                }]\n" +
                "            }, {\n" +
                "                \"name\": \"Channel\",\n" +
                "                \"field\": \"channels\",\n" +
                "                \"type\": \"multiple-select\",\n" +
                "                \"children\": [{\n" +
                "                    \"name\": \"Email\",\n" +
                "                    \"value\": \"email\"\n" +
                "                }, {\n" +
                "                    \"name\": \"SMS\",\n" +
                "                    \"value\": \"sms\"\n" +
                "                }, {\n" +
                "                    \"name\": \"LINE\",\n" +
                "                    \"value\": \"line\"\n" +
                "                }, {\n" +
                "                    \"name\": \"Facebook\",\n" +
                "                    \"value\": \"facebook\"\n" +
                "                }, {\n" +
                "                    \"name\": \"APP\",\n" +
                "                    \"value\": \"app\"\n" +
                "                }]\n" +
                "            }, {\n" +
                "                \"name\": \"Mobile\",\n" +
                "                \"field\": \"mobile\",\n" +
                "                \"type\": \"multiple-select\",\n" +
                "                \"children\": [{\n" +
                "                    \"name\": \"Mobile terminal\",\n" +
                "                    \"value\": \"1\"\n" +
                "                }, {\n" +
                "                    \"name\": \"Non-mobile terminal\",\n" +
                "                    \"value\": \"0\"\n" +
                "                }]\n" +
                "            }]\n" +
                "        }]\n" +
                "    }, {\n" +
                "        \"name\": \"Session\",\n" +
                "        \"type\": \"multiple-group\",\n" +
                "        \"value\": \"session\",\n" +
                "        \"children\": [{\n" +
                "            \"name\": \"Step\",\n" +
                "            \"field\": \"step\",\n" +
                "            \"type\": \"multiple-select\",\n" +
                "            \"children\": [{\n" +
                "                \"name\": \"Send Total\",\n" +
                "                \"value\": \"Send Total\"\n" +
                "            }, {\n" +
                "                \"name\": \"Send Success\",\n" +
                "                \"value\": \"Send Success\"\n" +
                "            }, {\n" +
                "                \"name\": \"Access to MM\",\n" +
                "                \"value\": \"Access to MM\"\n" +
                "            }, {\n" +
                "                \"name\": \"Access to Page\",\n" +
                "                \"value\": \"Access to Page\"\n" +
                "            }, {\n" +
                "                \"name\": \"Access to Item\",\n" +
                "                \"value\": \"Access to Item\"\n" +
                "            }]\n" +
                "        }, {\n" +
                "            \"name\": \"Filter\",\n" +
                "            \"type\": \"filter\",\n" +
                "            \"children\": [{\n" +
                "                \"name\": \"Member Type\",\n" +
                "                \"field\": \"memberTypes\",\n" +
                "                \"type\": \"multiple-select\",\n" +
                "                \"children\": [{\n" +
                "                    \"name\": \"FR\",\n" +
                "                    \"value\": \"FR\"\n" +
                "                }, {\n" +
                "                    \"name\": \"NFR\",\n" +
                "                    \"value\": \"NFR\"\n" +
                "                }, {\n" +
                "                    \"name\": \"HO\",\n" +
                "                    \"value\": \"HO\"\n" +
                "                }, {\n" +
                "                    \"name\": \"SV\",\n" +
                "                    \"value\": \"SV\"\n" +
                "                }, {\n" +
                "                    \"name\": \"DT\",\n" +
                "                    \"value\": \"DT\"\n" +
                "                }, {\n" +
                "                    \"name\": \"OT\",\n" +
                "                    \"value\": \"OT\"\n" +
                "                }]\n" +
                "            }, {\n" +
                "                \"name\": \"Channel\",\n" +
                "                \"field\": \"channels\",\n" +
                "                \"type\": \"multiple-select\",\n" +
                "                \"children\": [{\n" +
                "                    \"name\": \"Email\",\n" +
                "                    \"value\": \"email\"\n" +
                "                }, {\n" +
                "                    \"name\": \"SMS\",\n" +
                "                    \"value\": \"sms\"\n" +
                "                }, {\n" +
                "                    \"name\": \"LINE\",\n" +
                "                    \"value\": \"line\"\n" +
                "                }, {\n" +
                "                    \"name\": \"Facebook\",\n" +
                "                    \"value\": \"facebook\"\n" +
                "                }, {\n" +
                "                    \"name\": \"APP\",\n" +
                "                    \"value\": \"app\"\n" +
                "                }]\n" +
                "            }, {\n" +
                "                \"name\": \"Mobile\",\n" +
                "                \"field\": \"mobile\",\n" +
                "                \"type\": \"multiple-select\",\n" +
                "                \"children\": [{\n" +
                "                    \"name\": \"Mobile terminal\",\n" +
                "                    \"value\": \"1\"\n" +
                "                }, {\n" +
                "                    \"name\": \"Non-mobile terminal\",\n" +
                "                    \"value\": \"0\"\n" +
                "                }]\n" +
                "            }]\n" +
                "        }]\n" +
                "    }]");

        Integer total = templateFeignClient.getByMmCode(mmCode).getData().getTemplatePageTotal();
        JSONObject o = (JSONObject) jsonObject.get(0);
        JSONArray children = (JSONArray) o.get("children");
        JSONObject o2 = (JSONObject) children.get(0);
        JSONArray children2 = (JSONArray) o2.get("children");

        for (int i = 2; i <= total; i++) {
            children2.add(JSON.parseObject("{\"name\":\"Page " + i + "\",\"value\":\"" + i + "\"}"));
        }
        return jsonObject;
    }


}
