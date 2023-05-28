package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.stat.mapper.ProductAnalysisMapper;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import com.makro.mall.stat.service.ProductAnalysisService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【product_analysis】的数据库操作Service实现
 * @createDate 2022-07-06 17:06:37
 */
@Service
public class ProductAnalysisServiceImpl extends ServiceImpl<ProductAnalysisMapper, ProductAnalysis>
        implements ProductAnalysisService {

    @Override
    public List<ProductAnalysis> page(MakroPage<ProductAnalysis> result, String mmCode, Date startTime, Date endTime, String itemCode, String nameEn, String nameThai) {
        return getBaseMapper().page(result, mmCode, itemCode, nameEn, nameThai, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public List<ProductAnalysis> listForName(String mmCode, Date startTime, Date endTime, List<String> itemCode) {
        return getBaseMapper().listForName(mmCode, itemCode, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public List<ProductAnalysis> list(String mmCode, String goodsCode, Date startTime, Date endTime) {
        return getBaseMapper().compareProductList(mmCode, goodsCode, ClickHouseDateUtil.format2Str(startTime, endTime));

    }


    @Override
    public boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.format2Str(time)), true);
    }

    @Override
    public List<ProductAnalysis> list(String mmCode, Date startTime, Date endTime, List<String> itemCodes, String nameEn, String nameThai) {
        return getBaseMapper().list(mmCode, itemCodes, nameEn, nameThai, ClickHouseDateUtil.format2Str(startTime, endTime));
    }


}




