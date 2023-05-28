package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.dto.ChannelvisitorsDTO;
import com.makro.mall.stat.pojo.snapshot.ChannelVisitorConversion;
import com.makro.mall.stat.pojo.vo.BarChartVO;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【channel_visitor_conversion】的数据库操作Service
 * @createDate 2022-07-05 10:27:07
 */
public interface ChannelVisitorConversionService extends IService<ChannelVisitorConversion> {

    List<BarChartVO> list(String mmCode, Date startTime, Date endTime);

    List<ChannelvisitorsDTO> listForChannel(String mmCode, Date startTime, Date endTime);

    void saveChannelVisitorConversion(List<ChannelVisitorConversion> collect, Date time);

    boolean hasTimeData(Date time);

    Long sumChannelPie(String mmCode, Date startTime, Date endTime, String channel);
}
