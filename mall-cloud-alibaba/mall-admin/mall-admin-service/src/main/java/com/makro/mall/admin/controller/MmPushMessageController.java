package com.makro.mall.admin.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskMonitorDTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.dto.UserTaskMonitorDTO;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmPublishJobTaskReqVO;
import com.makro.mall.admin.service.MmPublishJobEmailTaskService;
import com.makro.mall.admin.service.MmPublishJobLineTaskService;
import com.makro.mall.admin.service.MmPublishJobSmsTaskService;
import com.makro.mall.admin.service.MmPublishJobTaskLogService;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/29 发布日志
 */
@Api(tags = "发布-推送消息")
@RestController
@RequestMapping("/api/v1/pushMessage")
@RequiredArgsConstructor
@Slf4j
public class MmPushMessageController {

    private final MmPublishJobEmailTaskService mmPublishJobEmailTaskService;
    private final MmPublishJobSmsTaskService mmPublishJobSmsTaskService;
    private final MmPublishJobLineTaskService mmPublishJobLineTaskService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;

    @ApiOperation(value = "更新发布中任务状态 日志", hidden = true)
    @PostMapping(value = "/task/updateState")
    public BaseResponse updateState(@RequestBody MmPublishJobTaskMonitorDTO dto) {
        if (ObjectUtil.equal(dto.getStatus(), MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus())) {
            boolean exists = mmPublishJobTaskLogService.getBaseMapper().exists(new LambdaQueryWrapper<MmPublishJobTaskLog>().eq(MmPublishJobTaskLog::getTaskId, dto.getId()));
            if (exists) {
                dto.setStatus(MessageTaskEnum.FAILED.getStatus());
            }
        }
        switch (dto.getChannel()) {
            case "email":
                log.info("updateState email dto:{}", dto);
                mmPublishJobEmailTaskService.update(new LambdaUpdateWrapper<MmPublishJobEmailTask>()
                        .set(MmPublishJobEmailTask::getStatus, dto.getStatus())
                        .eq(MmPublishJobEmailTask::getId, dto.getId())
                        .eq(MmPublishJobEmailTask::getStatus, MessageTaskEnum.PUSHING.getStatus()));
                return BaseResponse.success();
            case "line":
                log.info("updateState line dto:{}", dto);
                mmPublishJobLineTaskService.update(new LambdaUpdateWrapper<MmPublishJobLineTask>()
                        .set(MmPublishJobLineTask::getStatus, dto.getStatus())
                        .eq(MmPublishJobLineTask::getId, dto.getId())
                        .eq(MmPublishJobLineTask::getStatus, MessageTaskEnum.PUSHING.getStatus()));
                return BaseResponse.success();
            case "sms":
                log.info("updateState sms dto:{}", dto);
                mmPublishJobSmsTaskService.update(new LambdaUpdateWrapper<MmPublishJobSmsTask>()
                        .set(MmPublishJobSmsTask::getStatus, dto.getStatus())
                        .eq(MmPublishJobSmsTask::getId, dto.getId())
                        .eq(MmPublishJobSmsTask::getStatus, MessageTaskEnum.PUSHING.getStatus()));
                return BaseResponse.success();
            default:
                throw new BusinessException(AdminStatusCode.NO_SUCH_CHANNEL);
        }
    }

    @ApiOperation(value = "更新日志状态", hidden = true)
    @PostMapping(value = "/task/updateUserState")
    BaseResponse updateUserState(@RequestBody UserTaskMonitorDTO dto) {
        switch (dto.getChannel()) {
            case "email":
                log.info("updateUserState email dto:{}", dto);
                mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                        .set(MmPublishJobTaskLog::getStatus, dto.getStatus())
                        .set(MmPublishJobTaskLog::getSendTime, LocalDateTime.now())
                        .eq(MmPublishJobTaskLog::getChannel, "email")
                        .eq(MmPublishJobTaskLog::getTaskId, dto.getTaskId())
                        .eq(MmPublishJobTaskLog::getSendTo, dto.getSendTo())
                        .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
                return BaseResponse.success();
            case "line":
                log.info("updateUserState line dto:{}", dto);
                dto.getSendToForLine().forEach(line -> mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                        .set(MmPublishJobTaskLog::getStatus, dto.getStatus())
                        .set(MmPublishJobTaskLog::getSendTime, LocalDateTime.now())
                        .eq(MmPublishJobTaskLog::getChannel, "line")
                        .eq(MmPublishJobTaskLog::getTaskId, dto.getTaskId())
                        .eq(MmPublishJobTaskLog::getSendTo, line)
                        .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus())));
                return BaseResponse.success();
            case "sms":
                log.info("updateUserState sms dto:{}", dto);
                mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                        .set(MmPublishJobTaskLog::getStatus, dto.getStatus())
                        .set(MmPublishJobTaskLog::getSendTime, LocalDateTime.now())
                        .eq(MmPublishJobTaskLog::getChannel, "sms")
                        .eq(MmPublishJobTaskLog::getTaskId, dto.getTaskId())
                        .in(MmPublishJobTaskLog::getSendTo, MmCustomer.getReturnPhone(dto.getSendTo()))
                        .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
                return BaseResponse.success();
            default:
                throw new BusinessException(AdminStatusCode.NO_SUCH_CHANNEL);
        }
    }


    @ApiOperation(value = "推送日志分页 必传渠道 email/line/sms")
    @PostMapping(value = "/task/page")
    public BaseResponse page(@RequestBody SortPageRequest<MmPublishJobTaskReqDTO> req) {
        switch (req.getReq().getChannel()) {
            case "email":
                return BaseResponse.success(mmPublishJobEmailTaskService.page(req));
            case "line":
                return BaseResponse.success(mmPublishJobLineTaskService.page(req));
            case "sms":
                return BaseResponse.success(mmPublishJobSmsTaskService.page(req));
            default:
                throw new BusinessException(AdminStatusCode.NO_SUCH_CHANNEL);
        }
    }

    @ApiOperation(value = "推送日志用户分页 jobId email/line/sms")
    @PostMapping(value = "/task/userPage")
    public BaseResponse<MakroPage<MmPublishJobTaskLog>> userPage(@RequestBody SortPageRequest<MmPublishJobTaskReqVO> req) {
        return BaseResponse.success(mmPublishJobTaskLogService.userPage(req));
    }


    @ApiOperation(value = "取消任务")
    @PostMapping(value = "/task/cancel")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse cancel(@RequestBody @Valid MmPublishJobTaskMonitorDTO dto) {
        switch (dto.getChannel()) {
            case "email":
                log.info("cancel email dto:{}", dto);
                boolean update = mmPublishJobEmailTaskService.update(new LambdaUpdateWrapper<MmPublishJobEmailTask>()
                        .set(MmPublishJobEmailTask::getStatus, MessageTaskEnum.CANCELED.getStatus())
                        .eq(MmPublishJobEmailTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus())
                        .eq(MmPublishJobEmailTask::getId, dto.getId()));
                if (update) {
                    boolean email = mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                            .set(MmPublishJobTaskLog::getStatus, MessageSendEnum.CANCELED.getStatus())
                            .eq(MmPublishJobTaskLog::getChannel, "email")
                            .eq(MmPublishJobTaskLog::getTaskId, dto.getId()));
                    return BaseResponse.judge(email);
                }
                return BaseResponse.error("Task is pushing");
            case "line":
                log.info("cancel line dto:{}", dto);
                boolean update1 = mmPublishJobLineTaskService.update(new LambdaUpdateWrapper<MmPublishJobLineTask>()
                        .set(MmPublishJobLineTask::getStatus, MessageTaskEnum.CANCELED.getStatus())
                        .eq(MmPublishJobLineTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus())
                        .eq(MmPublishJobLineTask::getId, dto.getId()));
                if (update1) {
                    boolean line = mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                            .set(MmPublishJobTaskLog::getStatus, MessageSendEnum.CANCELED.getStatus())
                            .eq(MmPublishJobTaskLog::getChannel, "line")
                            .eq(MmPublishJobTaskLog::getTaskId, dto.getId()));
                    return BaseResponse.judge(line);
                }
                return BaseResponse.error("Task is pushing");
            case "sms":
                log.info("cancel sms dto:{}", dto);
                boolean update2 = mmPublishJobSmsTaskService.update(new LambdaUpdateWrapper<MmPublishJobSmsTask>()
                        .set(MmPublishJobSmsTask::getStatus, MessageTaskEnum.CANCELED.getStatus())
                        .eq(MmPublishJobSmsTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus())
                        .eq(MmPublishJobSmsTask::getId, dto.getId()));
                if (update2) {
                    boolean sms = mmPublishJobTaskLogService.update(new LambdaUpdateWrapper<MmPublishJobTaskLog>()
                            .set(MmPublishJobTaskLog::getStatus, MessageSendEnum.CANCELED.getStatus())
                            .eq(MmPublishJobTaskLog::getChannel, "sms")
                            .eq(MmPublishJobTaskLog::getTaskId, dto.getId()));
                    return BaseResponse.success(sms);
                }
                return BaseResponse.error("Task is pushing");
            default:
                throw new BusinessException(AdminStatusCode.NO_SUCH_CHANNEL);
        }
    }

    @ApiOperation(value = "消息重发")
    @PostMapping(value = "/task/again")
    BaseResponse again(@RequestBody MmPublishJobTaskLog dto) {
        switch (dto.getChannel()) {
            case "email":
                log.info("again email dto:{}", dto);
                mmPublishJobEmailTaskService.again(dto);
                return BaseResponse.success();
            case "line":
                log.info("again line dto:{}", dto);
                mmPublishJobLineTaskService.again(dto);
                return BaseResponse.success();
            case "sms":
                log.info("again sms dto:{}", dto);
                mmPublishJobSmsTaskService.again(dto);
                return BaseResponse.success();
            default:
                throw new BusinessException(AdminStatusCode.NO_SUCH_CHANNEL);
        }
    }


}
