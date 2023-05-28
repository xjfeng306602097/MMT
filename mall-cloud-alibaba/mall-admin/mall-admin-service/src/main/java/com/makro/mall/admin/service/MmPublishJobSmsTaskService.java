package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.MmPublishJobSmsTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJobSmsTask;
import com.makro.mall.admin.pojo.entity.MmPublishJobTaskLog;
import com.makro.mall.admin.pojo.vo.MmPublishJobSmsTaskRepVO;
import com.makro.mall.common.model.SortPageRequest;

import java.io.IOException;
import java.util.Set;

/**
 *
 */
public interface MmPublishJobSmsTaskService extends IService<MmPublishJobSmsTask> {

    IPage<MmPublishJobSmsTaskRepVO> page(SortPageRequest<MmPublishJobTaskReqDTO> req);

    Set<Long> getMmPublishTotal(String x);

    void scanMmPublishJobSmsTask();

    void again(MmPublishJobTaskLog dto);

    void send(String createdBy, MmPublishJobSmsTask task, MmPublishJobTaskLog c);

    void publishV2(MmPublishJobSmsTaskV2DTO mmPublishJobSmsTaskDTO) throws IOException;
}
