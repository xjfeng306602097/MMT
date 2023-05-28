package com.makro.mall.stat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.mapper.PageViewLogMapper;
import com.makro.mall.stat.pojo.dto.AverageVisitorVisitsPvMvDTO;
import com.makro.mall.stat.pojo.dto.VisitSummaryDTO;
import com.makro.mall.stat.pojo.dto.VisitorDataDTO;
import com.makro.mall.stat.pojo.metadata.PageViewLog;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.pojo.vo.VisitorDataVO;
import com.makro.mall.stat.service.PageViewLogService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【page_view_log】的数据库操作Service实现
 * @createDate 2022-06-30 13:28:31
 */
@Service
public class PageViewLogServiceImpl extends ServiceImpl<PageViewLogMapper, PageViewLog>
        implements PageViewLogService {

    @Override
    public List<BehaviorData> selectTimeBehaviorData(Date time) {
        return getBaseMapper().selectTimeBehaviorData(ClickHouseDateUtil.formatEqualsStr(time));
    }

    @Override
    public List<BehaviorData> selectTimeBehaviorDataMv(Date time) {
        return getBaseMapper().selectTimeBehaviorDataMv(ClickHouseDateUtil.formatEqualsStr(time));
    }


    @Override
    public List<AverageVisitorVisitsPvMvDTO> selectTimeAverageVisitorVisits(String channel, Date time) {
        return getBaseMapper().selectTimeAverageVisitorVisits(channel, ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<AverageVisitorVisitsPvMvDTO> selectTimeAverageVisitorVisitsMv(String channel, Date time) {
        return getBaseMapper().selectTimeAverageVisitorVisitsMv(channel, ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<ChannelVisitorConversion> selectTimeChannelVisitorConversion(Date time) {
        return getBaseMapper().selectTimeChannelVisitorConversion(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<VisitorDataVO> listForVisitDetails(MakroPage<VisitorDataVO> result, VisitorDataDTO visitorDataDTO) {
        return getBaseMapper().listForVisitDetails(result, visitorDataDTO, ClickHouseDateUtil.format2DateTimeStr(visitorDataDTO.getStartTime(), visitorDataDTO.getEndTime()));
    }

    @Override
    public List<AssemblyDataByMemberType> selectTimeMemberTypeClickThroughRate(Date time) {
        return getBaseMapper().selectYesterdayMemberTypeClickThroughRate(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public boolean add(PageViewLog pageViewLog) {
        baseMapper.insert(pageViewLog);
        return false;
    }

    @Override
    public List<VisitSummaryDTO> listVisitSummary(Date begin, Date end) {
        return baseMapper.listVisitSummary(ClickHouseDateUtil.format2DateTimeStr(begin, end));
    }

    @Override
    public List<VisitSummaryDTO> listVisitorSummary(Date begin, Date end) {
        return baseMapper.listVisitorSummary(ClickHouseDateUtil.format2DateTimeStr(begin, end));
    }

    @Override
    public Long selectTimeBehaviorDataTotalUv(Date time) {
        return getBaseMapper().selectTimeBehaviorDataTotalUv(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public Long selectTimeBehaviorDataTotalMv(Date time) {
        return getBaseMapper().selectTimeBehaviorDataTotalMv(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<BehaviorData> selectTimeBehaviorDataNewUv(Date time) {
        return getBaseMapper().selectTimeBehaviorDataNewUv(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public Long selectTimeBehaviorDataTotalNewUv(Date time) {
        return getBaseMapper().selectTimeBehaviorDataTotalNewUv(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<PagePageNo> listByPageNo(Date time) {
        return getBaseMapper().listByPageNo(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<PageChannelMemberType> listByTime(Date time) {
        return getBaseMapper().listByTime(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime) {
        return getBaseMapper().getCustomerExportDTO(mmCode, startTime, endTime);
    }
}



