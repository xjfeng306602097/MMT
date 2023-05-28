package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.dto.AverageVisitorVisitsPvMvDTO;
import com.makro.mall.stat.pojo.dto.VisitSummaryDTO;
import com.makro.mall.stat.pojo.dto.VisitorDataDTO;
import com.makro.mall.stat.pojo.metadata.PageViewLog;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.pojo.vo.VisitorDataVO;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【page_view_log】的数据库操作Service
 * @createDate 2022-06-30 13:28:31
 */
public interface PageViewLogService extends IService<PageViewLog> {

    /**
     * 功能描述:
     * select mmCode as mmcode, count(totalCount) uv , sum(totalCount) pv
     * from page_view_log
     * where eventDate = yesterday()
     * group by mmCode
     *
     * @return 搜寻行为数据中不包含mv的数据 按mmcode分组
     * @Author: 卢嘉俊
     * @Date: 2022/7/4 用户行为分析
     */
    List<BehaviorData> selectTimeBehaviorData(Date time);

    /**
     * 功能描述:
     *
     * @param time
     * @return 搜寻行为数据中mv的数据 按mmcode分组
     * @Author: 卢嘉俊
     * @Date: 2022/7/4 用户行为分析
     */
    List<BehaviorData> selectTimeBehaviorDataMv(Date time);

    List<AverageVisitorVisitsPvMvDTO> selectTimeAverageVisitorVisits(String channel, Date time);

    List<AverageVisitorVisitsPvMvDTO> selectTimeAverageVisitorVisitsMv(String channel, Date time);

    List<ChannelVisitorConversion> selectTimeChannelVisitorConversion(Date time);

    List<VisitorDataVO> listForVisitDetails(MakroPage<VisitorDataVO> result, VisitorDataDTO visitorDataDTO);

    List<AssemblyDataByMemberType> selectTimeMemberTypeClickThroughRate(Date time);

    boolean add(PageViewLog pageViewLog);

    List<VisitSummaryDTO> listVisitSummary(Date begin, Date end);

    List<VisitSummaryDTO> listVisitorSummary(Date begin, Date end);

    Long selectTimeBehaviorDataTotalUv(Date time);

    Long selectTimeBehaviorDataTotalMv(Date time);

    List<BehaviorData> selectTimeBehaviorDataNewUv(Date time);

    Long selectTimeBehaviorDataTotalNewUv(Date time);

    List<PagePageNo> listByPageNo(Date time);

    List<PageChannelMemberType> listByTime(Date time);

    List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime);
}
