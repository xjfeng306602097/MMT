package com.makro.mall.admin.controller.client;

import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.pojo.snapshot.PageTotalSuccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Api(hidden = true)
@RestController
@RequestMapping("/api/v1/publish/job/client")
@Slf4j
@RequiredArgsConstructor
public class MmPublishJobClient {
    private final MmPublishJobService mmPublishJobService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;
    private final MmPublishJobEmailTaskService mmPublishJobEmailTaskService;
    private final MmPublishJobSmsTaskService mmPublishJobSmsTaskService;
    private final MmPublishJobLineTaskService mmPublishJobLineTaskService;

    @ApiOperation(value = "扫描处理发布任务", hidden = true)
    @GetMapping(value = "/scanMmPublishJobTask")
    public BaseResponse<Boolean> scanMmPublishJobTask() {
        mmPublishJobEmailTaskService.scanMmPublishJobTask();
        return BaseResponse.success();
    }

    @ApiOperation(value = "扫描处理Line发布任务", hidden = true)
    @GetMapping(value = "/scanMmPublishJobLineTask")
    public BaseResponse<Boolean> scanMmPublishJobLineTask() {
        mmPublishJobLineTaskService.scanMmPublishJobTask();
        return BaseResponse.success();
    }

    @ApiOperation(value = "根据mmCode与渠道查询发送总数", hidden = true)
    @GetMapping(value = "/getMmPublishTotal")
    public BaseResponse<Integer> getMmPublishTotal(@RequestParam String mmCode, @RequestParam String channel) {
        return BaseResponse.success(mmPublishJobService.getMmPublishTotal(mmCode, channel));
    }

    @ApiOperation(value = "根据mmCode与发送总数,按照会员类型分类", hidden = true)
    @PostMapping(value = "/getMmPublishTotalByMemberType")
    public BaseResponse<List<AssemblyDataByMemberTypeDTO>> getMmPublishTotalGroupByMemberType(@RequestBody Set<String> mmCode) {
        return BaseResponse.success(mmPublishJobService.getMmPublishTotalGroupByMemberType(mmCode));
    }

    @ApiOperation(value = "扫描Sms发布任务", hidden = true)
    @GetMapping(value = "/scanMmPublishJobSmsTask")
    public BaseResponse<Boolean> scanMmPublishJobSmsTask() {
        mmPublishJobSmsTaskService.scanMmPublishJobSmsTask();
        return BaseResponse.success();
    }

    @ApiOperation(value = "获取mm发布总数和成功数", hidden = true)
    @GetMapping(value = "/pageTotalSuccess")
    public BaseResponse<List<PageTotalSuccess>> pageTotalSuccess(@RequestParam Date time) {
        return BaseResponse.success(mmPublishJobTaskLogService.pageTotalSuccess(time));
    }
}
