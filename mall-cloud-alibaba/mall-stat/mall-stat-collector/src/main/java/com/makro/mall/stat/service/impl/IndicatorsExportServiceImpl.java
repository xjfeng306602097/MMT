package com.makro.mall.stat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.stat.manager.AssemblyManager;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.constant.CustomerTypeEnum;
import com.makro.mall.stat.pojo.dto.*;
import com.makro.mall.stat.pojo.metadata.PageStayLog;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.pojo.vo.LineChartVO;
import com.makro.mall.stat.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorsExportServiceImpl implements IndicatorsExportService {

    private final IndicatorsService indicatorsService;
    private final BehaviorDataService behaviorDataService;
    private final ProductAnalysisService productAnalysisService;
    private final PagePageNoService pagePageNoService;
    private final PageChannelMemberTypeService pageChannelMemberTypeService;
    private final PageStaySummaryService pageStaySummaryService;
    private final AssemblyManager assemblyManager;
    private final PageStayLogService pageStayLogService;
    private final AssemblyService assemblyService;
    private final GoodsClickLogService goodsClickLogService;
    private final PageViewLogService pageViewLogService;
    private final CustomerFeignClient customerFeignClient;
    private final MmActivityFeignClient mmActivityFeignClient;


    private Integer no = 0;


    @Override
    public void exportExcel(String mmCode, Date startTime, Date endTime, HttpServletResponse response) {
        try {
            setResponse(response);

            if (StrUtil.isEmpty(mmCode)) {
                mmCode = "0";
            }
            no = 0;
            String start = DateUtil.format(startTime, "yyyy-MM-dd");
            String end = DateUtil.format(endTime, "yyyy-MM-dd");
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();

            HorizontalCellStyleStrategy horizontalCellStyleStrategy = getStrategy();

            clicksSummary(mmCode, start, end, excelWriter, horizontalCellStyleStrategy);
            mostVisitPage(mmCode, start, end, excelWriter, horizontalCellStyleStrategy);
            mostItemClickPage(mmCode, start, end, excelWriter, horizontalCellStyleStrategy);
            customerType(mmCode, startTime, endTime, excelWriter, horizontalCellStyleStrategy);
            channel(mmCode, startTime, endTime, excelWriter, horizontalCellStyleStrategy);
            clicksData(mmCode, startTime, endTime, excelWriter, horizontalCellStyleStrategy);
            pageStayTime(mmCode, startTime, endTime, start, end, excelWriter, horizontalCellStyleStrategy);
            customerTypeClicks(mmCode, start, end, excelWriter, horizontalCellStyleStrategy);
            mmCustomerExport(mmCode, start, end, excelWriter, horizontalCellStyleStrategy);

            excelWriter.finish();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("IndicatorsExportServiceImpl.exportExcel错误", e);
        }
    }


    private void mmCustomerExport(String mmCode, String start, String end, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        List<MmCustomerExportDTO> dtos = pageViewLogService.getCustomerExportDTO(StrUtil.equals(mmCode, "0") ? null : mmCode, start, end);
        List<MmCustomerExportDTO> pageStay = pageStayLogService.getCustomerExportDTO(StrUtil.equals(mmCode, "0") ? null : mmCode, start, end);
        List<MmCustomerExportDTO> goodsClick = goodsClickLogService.getCustomerExportDTO(StrUtil.equals(mmCode, "0") ? null : mmCode, start, end);
        List<String> customerIds = dtos.stream().map(MmCustomerExportDTO::getCustomerId).collect(Collectors.toList());
        List<MmCustomer> mmCustomersMap = customerFeignClient.getByIds(customerIds).getData();
        Map<String, MmCustomer> customerMap = CollUtil.isEmpty(mmCustomersMap) ? CollUtil.empty(ArrayList.class) : mmCustomersMap.stream().collect(Collectors.toMap(x -> String.valueOf(x.getId()), y -> y));
        List<String> mmCodes = dtos.stream().map(MmCustomerExportDTO::getCommunicationName).collect(Collectors.toList());
        Map<String, String> mmActivityMap = mmActivityFeignClient.getNameByCodes(mmCodes).getData();


        List<MmCustomerExportDTO> result = dtos.stream().peek(x -> {
            //填充页面停留
            List<MmCustomerExportDTO> pageStayList = pageStay.stream()
                    .filter(p -> StrUtil.equals(p.getCommunicationName(), x.getCommunicationName()))
                    .filter(p -> StrUtil.equals(p.getChannel(), x.getChannel()))
                    .filter(p -> StrUtil.equals(p.getCustomerType(), x.getCustomerType()))
                    .filter(p -> ObjectUtil.equals(p.getCustomerId(), x.getCustomerId()))
                    .filter(p -> StrUtil.equals(p.getPageNo(), x.getPageNo()))
                    .collect(Collectors.toList());
            long pageStaySum = pageStayList.stream().map(p -> ObjectUtil.isNull(p.getPageStayTime()) ? 0 : p.getPageStayTime())
                    .mapToLong(Number::longValue)
                    .sum();

            x.setPageStayTime((BigDecimal.valueOf(pageStaySum).divide(BigDecimal.valueOf(1000L), 2, RoundingMode.HALF_UP)));
            //填充商品点击次数
            List<MmCustomerExportDTO> goodsClickList = goodsClick.stream()
                    .filter(p -> StrUtil.equals(p.getCommunicationName(), x.getCommunicationName()))
                    .filter(p -> StrUtil.equals(p.getChannel(), x.getChannel()))
                    .filter(p -> StrUtil.equals(p.getCustomerType(), x.getCustomerType()))
                    .filter(p -> ObjectUtil.equals(p.getCustomerId(), x.getCustomerId()))
                    .filter(p -> StrUtil.equals(p.getPageNo(), x.getPageNo()))
                    .filter(p -> StrUtil.equals(p.getItemClicks(), x.getItemClicks()))
                    .collect(Collectors.toList());
            long goodsClickSum = goodsClickList.stream().map(p -> ObjectUtil.isNull(p.getTotalClicks()) ? 0 : p.getTotalClicks())
                    .mapToLong(p -> p)
                    .sum();
            x.setTotalClicks(goodsClickSum);
            //转换MM名称
            x.setCommunicationName(mmActivityMap.get(x.getCommunicationName()));
            //转换会员类型
            x.setCustomerType(CustomerTypeEnum.getCustomerType(x.getCustomerType()));
            MmCustomer mmCustomer = customerMap.get(x.getCustomerId());
            if (ObjectUtil.isNotNull(mmCustomer)) {
                //填充用户信息
                x.setMemberId(mmCustomer.getCustomerCode());
                x.setMobilePhone(mmCustomer.getPhone());
            }
        }).collect(Collectors.toList());

        WriteSheet writeSheet8 = EasyExcel.writerSheet(no += 1, "click Detail")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(MmCustomerExportDTO.class).build();
        excelWriter.write(result, writeSheet8);
    }

    @Override
    public void exportMixedPanelSummary(MixedPanelSummaryReqDTO dto, HttpServletResponse response) {
        try {
            setResponse(response);
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = getStrategy();
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            dto.setStep1("member");
            dto.setStep(List.of("Send Total", "Send Success", "Access to MM", "Access to Page", "Access to Item"));
            LineChartVO lineChartVO = indicatorsService.mixedPanelSummary(dto);
            // 列头
            List<List<String>> headList = new ArrayList<>();
            List<String> head1 = new ArrayList<>();
            head1.add("");
            head1.addAll(lineChartVO.getLabel());
            headList.add(head1);

            // 数据源
            List<List<String>> dataList = new ArrayList<>();
            lineChartVO.getList().forEach(x -> {
                List<String> list = new ArrayList<>();
                list.add("total");
                list.addAll(x.getValues());
                dataList.add(list);
            });

            WriteSheet writeSheet1 = EasyExcel.writerSheet(1, "Member Session").registerWriteHandler(horizontalCellStyleStrategy).build();
            excelWriter.write(headList, writeSheet1).write(dataList, writeSheet1);

            dto.setStep1("page");
            dto.setDataType(List.of("Click", "Item Click", "Page Stay Time", "Bounce Rate"));
            LineChartVO lineChartVO2 = indicatorsService.mixedPanelSummary(dto);
            // 列头
            List<List<String>> headList2 = new ArrayList<>();
            List<String> head2 = new ArrayList<>();
            head2.add("");
            head2.addAll(lineChartVO.getLabel());
            headList2.add(head2);

            // 数据源
            List<List<String>> dataList2 = new ArrayList<>();
            lineChartVO2.getList().forEach(x -> {
                List<String> list = new ArrayList<>();
                list.add(x.getName());
                list.addAll(x.getValues());
                dataList2.add(list);
            });

            WriteSheet writeSheet2 = EasyExcel.writerSheet(2, "Sheet2").registerWriteHandler(horizontalCellStyleStrategy).build();
            excelWriter.write(headList2, writeSheet2).write(dataList2, writeSheet2);

            excelWriter.finish();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("IndicatorsExportServiceImpl.exportMixedPanelSummary错误", e);
        }
    }

    private void setResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // 文件名
        String fileName = URLEncoder.encode("export", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }

    /**
     * 功能描述:
     *
     * @Param: 设置表格样式
     * @Return:
     * @Author: 卢嘉俊
     * @Date: 2022/8/24 数据分析
     */
    @NotNull
    private HorizontalCellStyleStrategy getStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headWriteCellStyle.setShrinkToFit(true);
        //内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        //背景色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        //设置 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置 水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setShrinkToFit(true);

        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }

    private void pageStayTime(String mmCode, Date startTime, Date endTime, String start, String end, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        if (ObjectUtil.equal(mmCode, "0")) {
            return;
        }
        Date now = new Date();
        Map<String, List<PageStaySummary>> currentMap = new HashMap<>();
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
            for (PageStaySummary summary : map.values()) {
                if (summary.isDt()) {
                    if (!currentMap.containsKey(CustomerTypeEnum.DT.getName())) {
                        currentMap.put(CustomerTypeEnum.DT.getName(), Lists.newArrayList());
                    }
                    currentMap.get(CustomerTypeEnum.DT.getName()).add(summary);
                }
                if (summary.isFr()) {
                    if (!currentMap.containsKey(CustomerTypeEnum.FR.getName())) {
                        currentMap.put(CustomerTypeEnum.FR.getName(), Lists.newArrayList());
                    }
                    currentMap.get(CustomerTypeEnum.FR.getName()).add(summary);
                }
                if (summary.isSv()) {
                    if (!currentMap.containsKey(CustomerTypeEnum.SV.getName())) {
                        currentMap.put(CustomerTypeEnum.SV.getName(), Lists.newArrayList());
                    }
                    currentMap.get(CustomerTypeEnum.SV.getName()).add(summary);
                }
                if (summary.isOt()) {
                    if (!currentMap.containsKey(CustomerTypeEnum.OT.getName())) {
                        currentMap.put(CustomerTypeEnum.OT.getName(), Lists.newArrayList());
                    }
                    currentMap.get(CustomerTypeEnum.OT.getName()).add(summary);
                }
                if (summary.isHo()) {
                    if (!currentMap.containsKey(CustomerTypeEnum.HO.getName())) {
                        currentMap.put(CustomerTypeEnum.HO.getName(), Lists.newArrayList());
                    }
                    currentMap.get(CustomerTypeEnum.HO.getName()).add(summary);
                }
                if (summary.isNfr()) {
                    if (!currentMap.containsKey(CustomerTypeEnum.NFR.getName())) {
                        currentMap.put(CustomerTypeEnum.NFR.getName(), Lists.newArrayList());
                    }
                    currentMap.get(CustomerTypeEnum.NFR.getName()).add(summary);
                }
            }
        }

        List<PageStaySummaryDTO> list6 = new ArrayList<>();
        CustomerTypeEnum.getNames().forEach(customerType -> {
            Wrapper<PageStaySummary> queryWrapper = new QueryWrapper<PageStaySummary>()
                    .select("page_no,channel,sum(visits)    AS visits,sum(stay_time) AS stayTime ,sum(bounce_rate_counts)    AS bounceRateCounts")
                    .between("cal_date", DateUtil.format(startTime, "yyyy-MM-dd"), DateUtil.format(endTime, "yyyy-MM-dd"))
                    .eq("mm_code", mmCode)
                    .eq(customerType.toLowerCase(), true)
                    .groupBy("page_no, channel");

            List<PageStaySummary> list = pageStaySummaryService.list(queryWrapper);
            list.addAll(currentMap.getOrDefault(customerType.toLowerCase(), new ArrayList<>()));
            List<PageStaySummaryDTO> collect = list.stream().map(x -> {
                PageStaySummaryDTO dto = new PageStaySummaryDTO();
                dto.setPageName(x.getPageNo());
                dto.setCustomerType(customerType);
                dto.setChannel(x.getChannel());
                Long num = x.getVisits() * 1000;
                dto.setAverageStay(NumberUtil.div(Double.valueOf(x.getStayTime()), num, 2));
                dto.setBounceRate(NumberUtil.div(Double.valueOf((x.getVisits() - x.getBounceRateCounts())), x.getVisits(), 2));
                return dto;
            }).collect(Collectors.toList());
            list6.addAll(collect);
        });

        WriteSheet writeSheet6 = EasyExcel.writerSheet(no += 1, "Page stay time(select a MM)")
                .registerWriteHandler(horizontalCellStyleStrategy).head(PageStaySummaryDTO.class).build();
        excelWriter.write(list6, writeSheet6);
    }

    private void mostItemClickPage(String mmCode, String start, String end, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        QueryWrapper<ProductAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("page_no as pageNo, sum(clicks) as clicks")
                .between("date", start, end)
                .eq(ObjectUtil.notEqual(mmCode, "0"), "mm_code", mmCode)
                .groupBy("page_no");
        List<ProductAnalysis> list = productAnalysisService.list(queryWrapper);
        DateTime endTime = DateUtil.parseDate(end);
        if (DateUtil.isSameDay(endTime, new Date())) {
            List<ProductAnalysis> collect = assemblyManager.getProductAnalyses(endTime).stream().filter(x -> {
                if (ObjectUtil.notEqual(mmCode, "0")) {
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
        list = list.stream().sorted(Comparator.comparing(ProductAnalysis::getClicks)).collect(Collectors.toList());

        List<MostVisitPageDTO> list3 = list.stream().map(x -> {
            MostVisitPageDTO mostVisitPageDTO = new MostVisitPageDTO();
            mostVisitPageDTO.setPageName(x.getPageNo());
            mostVisitPageDTO.setSummary(x.getClicks());
            return mostVisitPageDTO;
        }).collect(Collectors.toList());
        WriteSheet writeSheet3 = EasyExcel.writerSheet(no += 1, "Most item click page")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(MostVisitPageDTO.class).build();
        excelWriter.write(list3, writeSheet3);
    }

    private void customerTypeClicks(String mmCode, String start, String end, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        List<PageChannelMemberType> result = new ArrayList<>();
        for (CustomerTypeEnum value : CustomerTypeEnum.values()) {
            addResult(value.getName().toLowerCase(), mmCode, start, end, result);
        }

        WriteSheet writeSheet7 = EasyExcel.writerSheet(no += 1, "customer Type Clicks")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(PageChannelMemberType.class).build();
        excelWriter.write(result, writeSheet7);
    }

    private void addResult(String fr, String mmCode, String start, String end, List<PageChannelMemberType> result) {
        QueryWrapper<PageChannelMemberType> queryWrapper = new QueryWrapper<PageChannelMemberType>()
                .select("channel, sum(uv) as uv")
                .between("date", start, end)
                .groupBy("channel , " + fr)
                .eq("mm_code", mmCode)
                .eq(fr, true);
        List<PageChannelMemberType> list = pageChannelMemberTypeService.list(queryWrapper);

        DateTime endTime = DateUtil.parseDate(end);
        if (DateUtil.isSameDay(endTime, new Date())) {
            List<PageChannelMemberType> collect = assemblyManager.getPageChannelMemberType(endTime).stream()
                    .filter(x -> StrUtil.equals(x.getMmCode(), mmCode))
                    .filter(y -> {
                        switch (fr.toUpperCase()) {
                            case "FR":
                                return y.isFr();
                            case "NFR":
                                return y.isNfr();
                            case "HO":
                                return y.isHo();
                            case "SV":
                                return y.isSv();
                            case "DT":
                                return y.isDt();
                            case "OT":
                                return y.isOt();
                            default:
                                return false;
                        }
                    })
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                Map<String, Long> map = collect.stream()
                        .collect(Collectors.groupingBy(PageChannelMemberType::getChannel, Collectors.summingLong(PageChannelMemberType::getUv)));
                list = map.keySet().stream().map(key -> {
                    PageChannelMemberType pageChannelMemberType = new PageChannelMemberType();
                    pageChannelMemberType.setChannel(key);
                    pageChannelMemberType.setMemberType(fr);
                    pageChannelMemberType.setUv(map.get(key));
                    return pageChannelMemberType;
                }).collect(Collectors.toList());
            } else {
                list.forEach(x -> x.setUv(x.getUv() + collect.stream()
                        .filter(y -> StrUtil.equals(x.getChannel(), y.getChannel()))
                        .mapToLong(PageChannelMemberType::getUv)
                        .sum()));
            }
        }
        list = list.stream().sorted(Comparator.comparing(PageChannelMemberType::getUv)).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(list)) {
            result.addAll(list.stream().peek(x -> x.setMemberType(fr.toUpperCase())).collect(Collectors.toList()));
        }
    }

    private void clicksData(String mmCode, Date startTime, Date endTime, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        List<SummaryOfClicksDTO> list51 = indicatorsService.summaryOfClicks(mmCode, startTime, endTime, "email,sms,line,facebook,app");
        WriteSheet writeSheet5 = EasyExcel.writerSheet(no += 1, "Clicks Data").build();
        WriteTable writeTable1 = EasyExcel.writerTable(1)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(SummaryOfClicksDTO.class).build();
        excelWriter.write(list51, writeSheet5, writeTable1);

        if (ObjectUtil.notEqual(mmCode, "0")) {
            List<ProductAnalysis> list52 = productAnalysisService.list(new QueryWrapper<ProductAnalysis>()
                    .select("goods_code,name_en,name_thai,page_no,channel,sum(clicks) as clicks,sum(visitors) as visitors")
                    .eq("mm_code", mmCode)
                    .between("date", DateUtil.format(startTime, "yyyy-MM-dd"), DateUtil.format(endTime, "yyyy-MM-dd"))
                    .groupBy("goods_code,name_en,name_thai,page_no,channel"));

            if (DateUtil.isSameDay(endTime, new Date())) {
                List<ProductAnalysis> collect = assemblyManager.getProductAnalyses(endTime).stream().filter(x -> StrUtil.equals(x.getMmCode(), mmCode)).collect(Collectors.toList());
                if (CollUtil.isEmpty(list52)) {
                    list52 = collect;
                } else {
                    list52.forEach(x -> x.setClicks(x.getClicks() + collect.stream().filter(y -> StrUtil.equals(y.getPageNo(), x.getPageNo()) && StrUtil.equals(y.getGoodsCode(), x.getGoodsCode()))
                            .mapToLong(ProductAnalysis::getClicks).sum()));
                }
            }
            list52 = list52.stream().sorted(Comparator.comparing(ProductAnalysis::getClicks)).collect(Collectors.toList());

            WriteTable writeTable2 = EasyExcel.writerTable(2)
                    .registerWriteHandler(horizontalCellStyleStrategy)
                    .head(ProductAnalysis.class).build();
            excelWriter.write(list52, writeSheet5, writeTable2);
        }
    }

    private void channel(String mmCode, Date startTime, Date endTime, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        List<ChannelPieDTO> list5 = indicatorsService.channelPie(mmCode, startTime, endTime);
        WriteSheet writeSheet4 = EasyExcel.writerSheet(no += 1, "Channel")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(ChannelPieDTO.class).build();
        excelWriter.write(list5, writeSheet4);
    }

    private void customerType(String mmCode, Date startTime, Date endTime, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        List<CustomerTypePieDTO> list4 = indicatorsService.customerTypePie(mmCode, startTime, endTime);
        WriteSheet writeSheet3 = EasyExcel.writerSheet(no += 1, "Customer Type")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(CustomerTypePieDTO.class).build();
        excelWriter.write(list4, writeSheet3);
    }

    private void mostVisitPage(String mmCode, String start, String end, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        QueryWrapper<PagePageNo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("page_no as pageNo,sum(pv) as pv")
                .between("date", start, end)
                .eq("mm_code", mmCode)
                .groupBy("page_no");
        List<PagePageNo> pagePageNos = pagePageNoService.list(queryWrapper);
        DateTime endTime = DateUtil.parseDate(end);
        if (DateUtil.isSameDay(endTime, new Date())) {
            String finalMmCode = mmCode;
            List<PagePageNo> collect = assemblyManager.getPagePageNo(endTime).stream().filter(x -> StrUtil.equals(x.getMmCode(), finalMmCode)).collect(Collectors.toList());
            if (CollUtil.isEmpty(pagePageNos)) {
                pagePageNos = collect;
            } else {
                pagePageNos.forEach(x -> x.setPv(x.getPv() + collect.stream().filter(y -> StrUtil.equals(x.getPageNo(), y.getPageNo())).mapToLong(PagePageNo::getPv).sum()));
            }
        }
        pagePageNos = pagePageNos.stream().sorted(Comparator.comparing(PagePageNo::getPv)).limit(5L).collect(Collectors.toList());
        List<MostVisitPageDTO> list2 = pagePageNos.stream().map(x -> {
            MostVisitPageDTO mostVisitPageDTO = new MostVisitPageDTO();
            mostVisitPageDTO.setPageName(x.getPageNo());
            mostVisitPageDTO.setSummary(x.getPv());
            return mostVisitPageDTO;
        }).collect(Collectors.toList());
        WriteSheet writeSheet2 = EasyExcel.writerSheet(no += 1, "Most visit page")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(MostVisitPageDTO.class).build();
        excelWriter.write(list2, writeSheet2);
    }

    private void clicksSummary(String mmCode, String start, String end, ExcelWriter excelWriter, HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        QueryWrapper<BehaviorData> queryWrapper = new QueryWrapper<BehaviorData>()
                .eq("mm_code", mmCode)
                .between("date", start, end)
                .orderByDesc("date");
        List<BehaviorData> list1 = behaviorDataService.list(queryWrapper);
        DateTime endTime = DateUtil.parseDate(end);
        if (DateUtil.isSameDay(endTime, new Date())) {
            List<BehaviorData> collect = assemblyManager.getBehaviorData(endTime).stream().filter(x -> StrUtil.equals(x.getMmCode(), mmCode)).collect(Collectors.toList());
            list1.addAll(0, collect);
        }
        WriteSheet writeSheet1 = EasyExcel.writerSheet(no += 1, "Clicks Summary")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .head(BehaviorData.class).build();
        excelWriter.write(list1, writeSheet1);
    }
}
