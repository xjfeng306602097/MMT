package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmPublishJobTaskLog;
import com.makro.mall.admin.pojo.vo.MmPublishJobTaskReqVO;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.stat.pojo.snapshot.PageTotalSuccess;

import java.util.Date;
import java.util.List;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_TASK_LOG】的数据库操作Service
 * @createDate 2023-02-10 14:20:31
 */
public interface MmPublishJobTaskLogService extends IService<MmPublishJobTaskLog> {

    MakroPage<MmPublishJobTaskLog> userPage(SortPageRequest<MmPublishJobTaskReqVO> req);

    List<PageTotalSuccess> pageTotalSuccess(Date time);

}
