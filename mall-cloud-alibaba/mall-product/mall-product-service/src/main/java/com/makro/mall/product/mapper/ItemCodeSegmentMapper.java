package com.makro.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.product.pojo.entity.ItemCodeSegment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jincheng
 * @description 针对表【ITEM_CODE_SEGMENT(ITEM CODE关联SEGMENT表)】的数据库操作Mapper
 * @createDate 2022-10-25 13:20:26
 * @Entity .domain.ItemCodeSegment
 */
@Mapper
public interface ItemCodeSegmentMapper extends BaseMapper<ItemCodeSegment> {

}




