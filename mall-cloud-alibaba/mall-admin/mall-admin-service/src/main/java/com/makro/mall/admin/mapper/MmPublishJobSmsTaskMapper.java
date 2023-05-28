package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJobSmsTask;
import com.makro.mall.admin.pojo.vo.MmPublishJobSmsTaskRepVO;
import com.makro.mall.common.model.MakroPage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.makro.mall.admin.pojo.entity.MmPublishJobSmsTask
 */
@Mapper
public interface MmPublishJobSmsTaskMapper extends BaseMapper<MmPublishJobSmsTask> {

    Page<MmPublishJobSmsTaskRepVO> page(MakroPage<Object> objectMakroPage, String sortSql, MmPublishJobTaskReqDTO req);
}




