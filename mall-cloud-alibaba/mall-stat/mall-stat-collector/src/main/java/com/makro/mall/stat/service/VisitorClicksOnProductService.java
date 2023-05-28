package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【visitor_clicks_on_product】的数据库操作Service
 * @createDate 2022-07-04 14:11:36
 */
public interface VisitorClicksOnProductService extends IService<VisitorClicksOnProduct> {

    void saveVisitorClicksOnProduct(List<VisitorClicksOnProduct> all, String member, Date time);

    List<VisitorClicksOnProduct> list(String mmCode, Date startTime, Date endTime);

    boolean hasTimeData(Date time);
}
