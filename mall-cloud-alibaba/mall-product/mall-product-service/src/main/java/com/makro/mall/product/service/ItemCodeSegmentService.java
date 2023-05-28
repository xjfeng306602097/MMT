package com.makro.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.entity.ItemCodeSegment;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【ITEM_CODE_SEGMENT(ITEM CODE关联SEGMENT表)】的数据库操作Service
 * @createDate 2022-10-25 13:20:26
 */
public interface ItemCodeSegmentService extends IService<ItemCodeSegment> {

    List<String> getItemCodesBySegmentId(Long segmentId);
}
