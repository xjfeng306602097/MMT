package com.makro.mall.stat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.mapper.GoodsClickLogMapper;
import com.makro.mall.stat.pojo.metadata.GoodsClickLog;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct;
import com.makro.mall.stat.service.GoodsClickLogService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【goods_click_log】的数据库操作Service实现
 * @createDate 2022-06-30 13:28:28
 */
@Service
public class GoodsClickLogServiceImpl extends ServiceImpl<GoodsClickLogMapper, GoodsClickLog>
        implements GoodsClickLogService {
    @Override
    public boolean add(GoodsClickLog goodsClickLog) {
        baseMapper.insert(goodsClickLog);
        return true;
    }

    @Override
    public List<VisitorClicksOnProduct> selectTimeVisitorClicksOnProduct(String member, Date time) {
        return getBaseMapper().selectTimeVisitorClicksOnProduct(member, ClickHouseDateUtil.formatEqualsStr(time));
    }

    @Override
    public List<ProductAnalysis> selectTimeProductAnalysis(Date time) {
        return getBaseMapper().selectTimeProductAnalysis(ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime) {
        return getBaseMapper().getCustomerExportDTO(mmCode, startTime, endTime);
    }
}




