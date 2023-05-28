package com.makro.mall.stat.service;


import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.stat.pojo.dto.*;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import com.makro.mall.stat.pojo.snapshot.StayPageNo;
import com.makro.mall.stat.pojo.vo.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface IndicatorsService {


    /**
     * 功能描述: 基础数据统计接口
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    BasicDataVO basicData(String mmCode);

    /**
     * 功能描述: 实时访问（次数、人数）接口
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    RealTimeAccessVO realTimeAccess(String mmCode, Date queryDate);

    /**
     * 功能描述: 行为数据对比
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    LineChartVO behaviorData(String mmCode, Date startTime, Date endTime);

    /**
     * 功能描述: 不同类型访客点击商品次数
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    LineChartVO visitorClicksOnProduct(String mmCode, Date startTime, Date endTime);

    /**
     * 功能描述: 不同方式访问的访客平均访问次数
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    LineChartVO averageVisitorVisits(String mmCode, Date startTime, Date endTime);

    /**
     * 功能描述: 不同渠道推广访客转化率对比
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    List<BarChartVO> channelVisitorConversion(String mmCode, Date startTime, Date endTime);

    /**
     * 功能描述: 不同会员类型点击率对比
     * 用户行为分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/17
     */
    Collection<BarChartVO> memberTypeClickThroughRate(String mmCode, Date startTime, Date endTime, String memberTypeList);

    /**
     * 功能描述:商品分析
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/13 用户行为分析
     */
    IPage<ProductAnalysis> productAnalysis(String mmCode, Date startTime, Date endTime, String itemCode, Integer page, Integer limit, String nameEn, String nameThai);

    /**
     * 功能描述: 商品对比访问次数
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/13 用户行为分析
     */
    Map<String, LineChartVO> compareProduct(Date startTime, Date endTime, String goodsCode, String mmCode1, String mmCode2);


    /**
     * 功能描述:不同渠道来源访问人数
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/13 用户行为分析
     */
    LineChartVO channelvisitors(String mmCode, Date startTime, Date endTime);

    /**
     * 功能描述:不同方式访问的访客平均访问次数
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/13 用户行为分析
     */
    LineChartVO channelVisits(String mmCode, Date startTime, Date endTime);

    /**
     * 功能描述:访客数据
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/13 用户行为分析
     */
    IPage<VisitorDataVO> visitDetails(VisitorDataDTO visitorDataDTO);

    LineChartVO mostVisitPage(String mmCode, Date startTime, Date endTime, Boolean desc);

    List<CustomerTypePieDTO> customerTypePie(String mmCode, Date startTime, Date endTime);

    LineChartVO mostItemClickPage(String mmCode, Date startTime, Date endTime, Boolean desc);

    List<ChannelPieDTO> channelPie(String mmCode, Date startTime, Date endTime);

    List<SummaryOfClicksDTO> summaryOfClicks(String mmCode, Date startTime, Date endTime, String channels);

    List<SummaryOfClicksDetailVO> summaryOfClicksDetail(String mmCode, Date startTime, Date endTime, String channels);

    LineChartVO friends(String mmCode, Date startTime, Date endTime, String channels);

    LineChartVO pageStay(String mmCode, Date startTime, Date endTime, List<String> customerTypes, List<String> channels);

    IPage<StayPageNo> stayPageNo(Integer page, Integer limit, String mmCode, Date startTime, Date endTime, Boolean desc);

    IPage<PageStayDTO> pageStaySummary(PageStaySummaryRequest request);

    LineChartVO mixedPanelSummary(MixedPanelSummaryReqDTO dto);

    JSONArray mixedPanelSummaryJson(String mmCode);

    IPage<ProductAnalysis> itemClicks(Date startTime, Date endTime, SortPageRequest<ProductAnalysisDTO> req);
}
