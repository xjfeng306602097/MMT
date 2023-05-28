package com.makro.mall.stat.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.stat.pojo.dto.*;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import com.makro.mall.stat.pojo.snapshot.StayPageNo;
import com.makro.mall.stat.pojo.vo.*;
import com.makro.mall.stat.service.IndicatorsExportService;
import com.makro.mall.stat.service.IndicatorsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 指标接口
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/13
 */
@Api(tags = "指标接口")
@RestController
@RequestMapping("/api/v1/indicators")
@RequiredArgsConstructor
@Validated
public class IndicatorsController {

    private final IndicatorsService indicatorsService;
    private final IndicatorsExportService indicatorsExportService;

    /*********************************************3.14日************************************************************/

    @PostMapping("/mixedPanelSummary")
    public BaseResponse<LineChartVO> mixedPanelSummary(@RequestBody MixedPanelSummaryReqDTO dto) {
        return BaseResponse.success(indicatorsService.mixedPanelSummary(dto));
    }

    @GetMapping("/mixedPanelSummaryJson")
    public BaseResponse<JSONArray> mixedPanelSummaryJson(String mmCode) {
        return BaseResponse.success(indicatorsService.mixedPanelSummaryJson(mmCode));
    }

    @PostMapping("/exportMixedPanelSummary")
    public void exportMixedPanelSummary(@RequestBody MixedPanelSummaryReqDTO dto, HttpServletResponse response) {
        indicatorsExportService.exportMixedPanelSummary(dto, response);
    }

    /*********************************************基础数据************************************************************/
    @ApiOperation(value = "基础数据统计接口")
    @GetMapping("/basicData")
    public BaseResponse<BasicDataVO> basicData(@RequestParam(required = false) String mmCode) {
        return BaseResponse.success(indicatorsService.basicData(mmCode));
    }

    @ApiOperation(value = "实时访问（次数、人数）接口")
    @GetMapping("/realTimeAccess")
    public BaseResponse<RealTimeAccessVO> realTimeAccess(String mmCode, @RequestParam(required = false) String queryDate) {
        Date parse = null;
        if (!StrUtil.isBlank(queryDate)) {
            parse = DateUtil.parse(queryDate, "yyyy-MM-dd");
        }
        return BaseResponse.success(indicatorsService.realTimeAccess(mmCode, parse));
    }

    /*********************************************行为分析************************************************************/

    @ApiOperation(value = "行为数据对比")
    @GetMapping("/behaviorData")
    public BaseResponse<LineChartVO> behaviorData(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.behaviorData(mmCode, startTime, endTime));
    }

    @ApiOperation(value = "不同类型访客点击商品次数")
    @GetMapping("/productClicksByVisitors")
    public BaseResponse<LineChartVO> visitorClicksOnProduct(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.visitorClicksOnProduct(mmCode, startTime, endTime));
    }

    @ApiOperation(value = "不同方式访问的访客平均访问次数")
    @GetMapping("/averageVisitorVisits")
    public BaseResponse<LineChartVO> averageVisitorVisits(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.averageVisitorVisits(mmCode, startTime, endTime));
    }

    @ApiOperation(value = "不同渠道推广访客转化率对比")
    @GetMapping("/channelVisitorConversion")
    public BaseResponse<List<BarChartVO>> channelVisitorConversion(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.channelVisitorConversion(mmCode, startTime, endTime));
    }

    @ApiOperation(value = "不同会员类型点击率对比")
    @GetMapping("/memberTypeClickThroughRate")
    public BaseResponse<Collection<BarChartVO>> memberTypeClickThroughRate(String mmCode, Date startTime, Date endTime, String memberTypes) {
        return BaseResponse.success(indicatorsService.memberTypeClickThroughRate(mmCode, startTime, endTime, memberTypes));
    }


    /*********************************************商品分析************************************************************/

    @ApiOperation(value = "商品分析")
    @GetMapping("/productAnalysis")
    public BaseResponse<IPage<ProductAnalysis>> productAnalysis(String mmCode,
                                                                Date startTime,
                                                                Date endTime,
                                                                @ApiParam(value = "商品编码") String goodsCode,
                                                                Integer page,
                                                                Integer limit,
                                                                String nameEn,
                                                                String nameThai) {
        return BaseResponse.success(indicatorsService.productAnalysis(mmCode, startTime, endTime, goodsCode, page, limit, nameEn, nameThai));
    }

    @ApiOperation(value = "商品对比访问次数")
    @GetMapping("/compareProduct")
    public BaseResponse<Map<String, LineChartVO>> compareProduct(@NotNull String mmCode,
                                                                 String mmCode2,
                                                                 @NotNull Date startTime,
                                                                 @NotNull Date endTime,
                                                                 @ApiParam(value = "商品编码") @NotNull String goodsCode) {
        return BaseResponse.success(indicatorsService.compareProduct(startTime, endTime, goodsCode, mmCode, mmCode2));
    }

    /*********************************************来源分析************************************************************/
    @ApiOperation(value = "不同方式访问的访客平均访问次数")
    @GetMapping("/channelVisits")
    public BaseResponse<LineChartVO> channelVisits(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.channelVisits(mmCode, startTime, endTime));
    }

    @ApiOperation(value = "不同渠道来源访问人数")
    @GetMapping("/channelVisitors")
    public BaseResponse<LineChartVO> channelVisitors(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.channelvisitors(mmCode, startTime, endTime));
    }

    /*********************************************访客数据************************************************************/

    @ApiOperation(value = "访客数据")
    @GetMapping("/visitDetails")
    public BaseResponse<IPage<VisitorDataVO>> visitDetails(@ApiParam(value = "页码", defaultValue = "1") @NotNull Integer page,
                                                           @ApiParam(value = "每页数量", defaultValue = "100") @NotNull Integer limit,
                                                           @NotNull Date startTime,
                                                           @NotNull Date endTime,
                                                           @ApiParam(value = "设备类型 IOS/Android/Windows/Mac/Linux/....") String platform,
                                                           @ApiParam(value = "进入渠道 email/sms/line/facebook/app") String channel,
                                                           @ApiParam(value = "IP地址") String ip,
                                                           String mmCode) {
        VisitorDataDTO visitorDataDTO = new VisitorDataDTO(page, limit, startTime, endTime, platform, channel, ip, mmCode);
        return BaseResponse.success(indicatorsService.visitDetails(visitorDataDTO));
    }

    /*********************************************模块2************************************************************/
    @GetMapping("/mostVisitPage")
    public BaseResponse<LineChartVO> mostVisitPage(String mmCode, Date startTime, Date endTime,
                                                   @ApiParam("是否倒叙") @RequestParam(required = false) Boolean desc) {
        return BaseResponse.success(indicatorsService.mostVisitPage(mmCode, startTime, endTime, desc == null || desc));
    }

    @GetMapping("/mostItemClickPage")
    public BaseResponse<LineChartVO> mostItemClickPage(String mmCode, Date startTime, Date endTime,
                                                       @ApiParam("是否倒叙") @RequestParam(required = false) Boolean desc) {
        return BaseResponse.success(indicatorsService.mostItemClickPage(mmCode, startTime, endTime, desc == null || desc));
    }

    /*********************************************模块3************************************************************/
    @GetMapping("/customerTypePie")
    public BaseResponse<List<CustomerTypePieDTO>> customerTypePie(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.customerTypePie(mmCode, startTime, endTime));
    }

    @GetMapping("/channelPie")
    public BaseResponse<List<ChannelPieDTO>> channelPie(String mmCode, Date startTime, Date endTime) {
        return BaseResponse.success(indicatorsService.channelPie(mmCode, startTime, endTime));
    }

    /*********************************************模块4************************************************************/
    @GetMapping("/summaryOfClicks")
    public BaseResponse<List<SummaryOfClicksDTO>> summaryOfClicks(String mmCode, Date startTime, Date endTime,
                                                                  @ApiParam("渠道 ,分割") @RequestParam(required = false) String channels) {
        return BaseResponse.success(indicatorsService.summaryOfClicks(mmCode, startTime, endTime, channels));
    }

    @GetMapping("/summaryOfClicksDetail")
    public BaseResponse<List<SummaryOfClicksDetailVO>> summaryOfClicksDetail(String mmCode, Date startTime, Date endTime,
                                                                             @ApiParam("渠道 ,分割") @RequestParam(required = false) String channels) {
        return BaseResponse.success(indicatorsService.summaryOfClicksDetail(mmCode, startTime, endTime, channels));
    }

    @PostMapping("/ItemClicks")
    public BaseResponse<IPage<ProductAnalysis>> ItemClicks(@RequestParam Date startTime,
                                                           @RequestParam Date endTime,
                                                           @RequestBody SortPageRequest<ProductAnalysisDTO> req) {
        return BaseResponse.success(indicatorsService.itemClicks(startTime, endTime, req));
    }

    /*********************************************模块5************************************************************/

    @GetMapping("/friends")
    public BaseResponse<LineChartVO> friends(String mmCode, Date startTime, Date endTime,
                                             @ApiParam("渠道 ,分割") String channels) {
        return BaseResponse.success(indicatorsService.friends(mmCode, startTime, endTime, channels));
    }

    @GetMapping("/pageStay")
    public BaseResponse<LineChartVO> pageStay(String mmCode, Date startTime, Date endTime, String customerTypes,
                                              @ApiParam("渠道 ,分割") String channels) {
        return BaseResponse.success(indicatorsService.pageStay(mmCode, startTime, endTime, List.of(customerTypes.split(",")), List.of(channels.split(","))));
    }

    @GetMapping("/stayPageNo")
    public BaseResponse<IPage<StayPageNo>> stayPageNo(@NotNull Integer page, @NotNull Integer limit,
                                                      @NotNull String mmCode, @NotNull Date startTime,
                                                      @NotNull Date endTime, @ApiParam("是否倒叙") @NotNull Boolean desc) {
        return BaseResponse.success(indicatorsService.stayPageNo(page, limit, mmCode, startTime, endTime, desc));
    }

    @PostMapping("/pageStaySummary")
    public BaseResponse<IPage<PageStayDTO>> pageStaySummary(@Valid @RequestBody PageStaySummaryRequest pageStaySummaryRequest) {
        return BaseResponse.success(indicatorsService.pageStaySummary(pageStaySummaryRequest));
    }

    @GetMapping("/export")
    public void export(String mmCode, Date startTime, Date endTime, HttpServletResponse response) {
        indicatorsExportService.exportExcel(mmCode, startTime, endTime, response);
    }

}
