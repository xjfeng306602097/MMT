package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJobLineTask;
import com.makro.mall.admin.pojo.vo.MmPublishJobLineTaskRepVO;
import com.makro.mall.common.model.MakroPage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_LINE_TASK】的数据库操作Mapper
 * @createDate 2022-07-29 10:12:14
 * @Entity .domain.MmPublishJobLineTask
 */
@Mapper
public interface MmPublishJobLineTaskMapper extends BaseMapper<MmPublishJobLineTask> {

    Page<MmPublishJobLineTaskRepVO> page(MakroPage<Object> objectMakroPage, String sortSql, MmPublishJobTaskReqDTO req);
}




