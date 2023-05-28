package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.ChannelVisitorConversionMapper;
import com.makro.mall.stat.pojo.dto.ChannelvisitorsDTO;
import com.makro.mall.stat.pojo.snapshot.ChannelVisitorConversion;
import com.makro.mall.stat.pojo.vo.BarChartVO;
import com.makro.mall.stat.service.ChannelVisitorConversionService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【channel_visitor_conversion】的数据库操作Service实现
 * @createDate 2022-07-05 10:27:07
 */
@Service
public class ChannelVisitorConversionServiceImpl extends ServiceImpl<ChannelVisitorConversionMapper, ChannelVisitorConversion>
        implements ChannelVisitorConversionService {

    @Override
    public List<BarChartVO> list(String mmCode, Date startTime, Date endTime) {
        return getBaseMapper().list(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public List<ChannelvisitorsDTO> listForChannel(String mmCode, Date startTime, Date endTime) {
        return getBaseMapper().listForChannel(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime));

    }

    @Override
    public void saveChannelVisitorConversion(List<ChannelVisitorConversion> collect, Date time) {
        getBaseMapper().saveChannelVisitorConversion(collect, ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.format2Str(time)), true);
    }

    @Override
    public Long sumChannelPie(String mmCode, Date startTime, Date endTime, String channel) {
        return getBaseMapper().sumChannelPie(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime), channel);
    }
}




