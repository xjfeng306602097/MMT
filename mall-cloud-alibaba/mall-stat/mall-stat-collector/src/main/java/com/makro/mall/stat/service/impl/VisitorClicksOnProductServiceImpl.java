package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.VisitorClicksOnProductMapper;
import com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct;
import com.makro.mall.stat.service.VisitorClicksOnProductService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【visitor_clicks_on_product】的数据库操作Service实现
 * @createDate 2022-07-04 14:11:36
 */
@Service
public class VisitorClicksOnProductServiceImpl extends ServiceImpl<VisitorClicksOnProductMapper, VisitorClicksOnProduct>
        implements VisitorClicksOnProductService {

    @Override
    public void saveVisitorClicksOnProduct(List<VisitorClicksOnProduct> list, String member, Date time) {
        getBaseMapper().saveVisitorClicksOnProduct(list, member, ClickHouseDateUtil.format2Str(time));
    }

    @Override
    public List<VisitorClicksOnProduct> list(String mmCode, Date startTime, Date endTime) {
        return getBaseMapper().list(mmCode, ClickHouseDateUtil.format2Str(startTime, endTime));
    }

    @Override
    public boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.formatEqualsStr(time)), true);
    }
}




