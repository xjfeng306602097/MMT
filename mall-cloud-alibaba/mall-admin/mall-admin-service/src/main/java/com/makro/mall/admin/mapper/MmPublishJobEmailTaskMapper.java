package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJobEmailTask;
import com.makro.mall.admin.pojo.vo.MmPublishJobEmailTaskRepVO;
import com.makro.mall.common.model.MakroPage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_EMAIL_TASK】的数据库操作Mapper
 * @createDate 2022-06-01 15:49:18
 * @Entity com.makro.mall.admin.pojo.entity.MmPublishJobEmailTask
 */
@Mapper
public interface MmPublishJobEmailTaskMapper extends BaseMapper<MmPublishJobEmailTask> {

    Page<MmPublishJobEmailTaskRepVO> page(MakroPage<MmPublishJobEmailTaskRepVO> objectMakroPage, String sortSql, MmPublishJobTaskReqDTO req);
}




