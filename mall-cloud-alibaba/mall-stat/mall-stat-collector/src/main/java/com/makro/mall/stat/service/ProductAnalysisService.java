package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【product_analysis】的数据库操作Service
 * @createDate 2022-07-06 17:06:37
 */
public interface ProductAnalysisService extends IService<ProductAnalysis> {

    /**
     * 功能描述: 查找商品分页
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/7 用户行为分析
     */
    List<ProductAnalysis> page(MakroPage<ProductAnalysis> result, String mmCode, Date startTime, Date endTime, String itemCode, String nameEn, String nameThai);

    /**
     * 功能描述: 反查商品 统计数据
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/7 用户行为分析
     */
    List<ProductAnalysis> listForName(String mmCode, Date startTime, Date endTime, List<String> itemCode);

    List<ProductAnalysis> list(String mmCode, String goodsCode, Date startTime, Date endTime);

    boolean hasTimeData(Date time);

    List<ProductAnalysis> list(String mmCode, Date startTime, Date endTime, List<String> itemCodes, String nameEn, String nameThai);
}
