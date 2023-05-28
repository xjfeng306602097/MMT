package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.admin.pojo.entity.MmFlow;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.makro.mall.admin.pojo.entity.MmFlow
 */
@Mapper
public interface MmFlowMapper extends BaseMapper<MmFlow> {

    MmFlow getLastOne(String code);
}




