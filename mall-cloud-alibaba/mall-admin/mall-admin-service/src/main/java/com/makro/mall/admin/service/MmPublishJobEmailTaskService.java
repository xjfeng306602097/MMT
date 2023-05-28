package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.MmPublishJobEmailTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJobEmailTask;
import com.makro.mall.admin.pojo.entity.MmPublishJobTaskLog;
import com.makro.mall.admin.pojo.vo.MmPublishJobEmailTaskRepVO;
import com.makro.mall.common.model.SortPageRequest;

import java.io.IOException;
import java.util.Set;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_EMAIL_TASK】的数据库操作Service
 * @createDate 2022-06-01 15:49:18
 */
public interface MmPublishJobEmailTaskService extends IService<MmPublishJobEmailTask> {
    void scanMmPublishJobTask();

    /**
     * @Param: channel 渠道,email/sms/line/facebook/app
     * @Return: 客户编码列表
     * @Author: 卢嘉俊
     * @Date: 2022/7/5 用户行为分析
     */
    Set<Long> getMmPublishTotal(String mmCode);

    IPage<MmPublishJobEmailTaskRepVO> page(SortPageRequest<MmPublishJobTaskReqDTO> req);

    void again(MmPublishJobTaskLog dto);

    void send(MmPublishJobEmailTask task, MmPublishJobTaskLog customer);

    void publishV2(MmPublishJobEmailTaskV2DTO publishJobTaskDTO) throws IOException;
}
