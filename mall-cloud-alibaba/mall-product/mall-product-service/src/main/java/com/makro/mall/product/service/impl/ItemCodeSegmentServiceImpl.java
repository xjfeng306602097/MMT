package com.makro.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ItemCodeSegmentMapper;
import com.makro.mall.product.pojo.entity.ItemCodeSegment;
import com.makro.mall.product.service.ItemCodeSegmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jincheng
 * @description 针对表【ITEM_CODE_SEGMENT(ITEM CODE关联SEGMENT表)】的数据库操作Service实现
 * @createDate 2022-10-25 13:20:26
 */
@Service
public class ItemCodeSegmentServiceImpl extends ServiceImpl<ItemCodeSegmentMapper, ItemCodeSegment>
        implements ItemCodeSegmentService {

    @Override
    public List<String> getItemCodesBySegmentId(Long segmentId) {
        return list(new LambdaQueryWrapper<ItemCodeSegment>().eq(ItemCodeSegment::getSegmentId, segmentId).select(ItemCodeSegment::getItemCode)).stream().map(ItemCodeSegment::getItemCode).collect(Collectors.toList());

    }
}




