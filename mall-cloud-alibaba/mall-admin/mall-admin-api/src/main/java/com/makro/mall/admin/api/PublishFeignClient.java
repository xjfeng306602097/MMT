package com.makro.mall.admin.api;


import com.makro.mall.admin.api.fallback.PublishFeignFallbackClient;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskMonitorDTO;
import com.makro.mall.admin.pojo.dto.UserTaskMonitorDTO;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.pojo.snapshot.PageTotalSuccess;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2021/10/9
 */
@FeignClient(value = "makro-admin", fallback = PublishFeignFallbackClient.class, contextId = "publish-client")
public interface PublishFeignClient {

    String MM_PUBLISH_JOB_CONTROLLER = "/api/v1/publish/job/client";

    @GetMapping(value = MM_PUBLISH_JOB_CONTROLLER + "/scanMmPublishJobTask")
    BaseResponse<Boolean> scanMmPublishJobTask();

    @GetMapping(value = MM_PUBLISH_JOB_CONTROLLER + "/scanMmPublishJobLineTask")
    BaseResponse<Boolean> scanMmPublishJobLineTask();

    @GetMapping(value = MM_PUBLISH_JOB_CONTROLLER + "/getMmPublishTotal")
    BaseResponse<Integer> getMmPublishTotal(@RequestParam String mmCode, @RequestParam String channel);

    @PostMapping(value = MM_PUBLISH_JOB_CONTROLLER + "/getMmPublishTotalByMemberType")
    BaseResponse<List<AssemblyDataByMemberTypeDTO>> getMmPublishTotalGroupByMemberType(@RequestBody Set<String> mmCode);

    @PostMapping(value = "/api/v1/pushMessage/task/updateState")
    BaseResponse updateState(@RequestBody MmPublishJobTaskMonitorDTO dto);

    @PostMapping(value = "/api/v1/pushMessage/task/updateUserState")
    BaseResponse updateUserState(@RequestBody UserTaskMonitorDTO dto);

    @GetMapping(value = MM_PUBLISH_JOB_CONTROLLER + "/scanMmPublishJobSmsTask")
    BaseResponse<Boolean> scanMmPublishJobSmsTask();

    @GetMapping(value = MM_PUBLISH_JOB_CONTROLLER + "/pageTotalSuccess")
    BaseResponse<List<PageTotalSuccess>> pageTotalSuccess(@RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date time);
}
