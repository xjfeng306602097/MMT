package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.dto.MmActivityPageReqDTO;
import com.makro.mall.admin.pojo.entity.MmActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.admin.pojo.entity.MmActivity
 */
@Mapper
public interface MmActivityMapper extends BaseMapper<MmActivity> {

    List<MmActivity> list(Page<MmActivity> page, MmActivityPageReqDTO dto);

    void updateBatchForFailure(List<Long> ids);
}




