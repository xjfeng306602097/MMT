package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.metadata.GoodsClickLog;
import com.makro.mall.stat.pojo.snapshot.PagePv;
import com.makro.mall.stat.pojo.snapshot.ProductAnalysis;
import com.makro.mall.stat.pojo.snapshot.VisitorClicksOnProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【goods_click_log】的数据库操作Mapper
 * @createDate 2022-06-30 13:28:28
 * @Entity com.makro.mall.stat.pojo.metadata.GoodsClickLog
 */
@Mapper
public interface GoodsClickLogMapper extends BaseMapper<GoodsClickLog> {

    List<PagePv> selectYesterdayPagePv();

    List<VisitorClicksOnProduct> selectTimeVisitorClicksOnProduct(String member, String s);

    List<ProductAnalysis> selectTimeProductAnalysis(String s);

    List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime);
}




