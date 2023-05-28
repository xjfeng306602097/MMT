package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.dto.AverageVisitorVisitsPvMvDTO;
import com.makro.mall.stat.pojo.dto.VisitSummaryDTO;
import com.makro.mall.stat.pojo.dto.VisitorDataDTO;
import com.makro.mall.stat.pojo.metadata.PageViewLog;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.pojo.vo.VisitorDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【page_view_log】的数据库操作Mapper
 * @createDate 2022-06-30 13:28:31
 * @Entity com.makro.mall.stat.pojo.metadata.PageViewLog
 */
@Mapper
public interface PageViewLogMapper extends BaseMapper<PageViewLog> {

    List<BehaviorData> selectTimeBehaviorData(String sql);

    List<BehaviorData> selectTimeBehaviorDataMv(String sql);

    List<AverageVisitorVisitsPvMvDTO> selectTimeAverageVisitorVisits(String channel, String sql);

    List<AverageVisitorVisitsPvMvDTO> selectTimeAverageVisitorVisitsMv(String channel, String s);

    List<ChannelVisitorConversion> selectTimeChannelVisitorConversion(String sql);

    List<VisitorDataVO> listForVisitDetails(@Param("result") MakroPage<VisitorDataVO> result, @Param("visitorDataDTO") VisitorDataDTO visitorDataDTO, @Param("sql") String sql);

    List<AssemblyDataByMemberType> selectYesterdayMemberTypeClickThroughRate(String s);

    List<VisitSummaryDTO> listVisitSummary(@Param("sql") String sql);

    List<VisitSummaryDTO> listVisitorSummary(@Param("sql") String sql);

    Long selectTimeBehaviorDataTotalUv(String sql);

    Long selectTimeBehaviorDataTotalMv(String sql);

    List<BehaviorData> selectTimeBehaviorDataNewUv(String sql);

    Long selectTimeBehaviorDataTotalNewUv(String sql);

    List<PagePageNo> listByPageNo(String sql);

    List<PageChannelMemberType> listByTime(String sql);

    List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime);
}




