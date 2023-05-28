package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.metadata.GoodsClickLog;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【goods_click_log】的数据库操作Service
 * @createDate 2022-06-30 13:28:28
 */
public interface GoodsClickLogService extends IService<GoodsClickLog> {

    boolean add(GoodsClickLog goodsClickLog);

    List<VisitorClicksOnProduct> selectTimeVisitorClicksOnProduct(String member, Date time);

    List<ProductAnalysis> selectTimeProductAnalysis(Date time);

    List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime);
}
