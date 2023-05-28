package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.MmPublishJobLineTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmPublishJobLineTask;
import com.makro.mall.admin.pojo.entity.MmPublishJobTaskLog;
import com.makro.mall.admin.pojo.vo.MmPublishJobLineTaskRepVO;
import com.makro.mall.common.model.SortPageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_LINE_TASK】的数据库操作Service
 * @createDate 2022-07-29 10:12:14
 */
public interface MmPublishJobLineTaskService extends IService<MmPublishJobLineTask> {

    void scanMmPublishJobTask();

    IPage<MmPublishJobLineTaskRepVO> page(SortPageRequest<MmPublishJobTaskReqDTO> req);

    Set<Long> getMmPublishTotal(String x);

    void again(MmPublishJobTaskLog dto);

    void send(MmPublishJobLineTask task, List<String> x, MmActivity activity);

    void publishV2(MmPublishJobLineTaskV2DTO publishJobTaskDTO) throws IOException;

}
