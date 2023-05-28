package com.makro.mall.admin.controller;

import com.makro.mall.admin.pojo.dto.MmPublishJobEmailTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobLineTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobSmsTaskV2DTO;
import com.makro.mall.admin.service.MmPublishJobEmailTaskService;
import com.makro.mall.admin.service.MmPublishJobLineTaskService;
import com.makro.mall.admin.service.MmPublishJobSmsTaskService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/3/10
 */
@Api(tags = "发布-推送消息-v2")
@RestController
@RequestMapping("/api/v1/pushMessage/v2")
@RequiredArgsConstructor
@Slf4j
public class MmPushMessageV2Controller {

    private final MmPublishJobEmailTaskService mmPublishJobEmailTaskService;
    private final MmPublishJobLineTaskService mmPublishJobLineTaskService;
    private final MmPublishJobSmsTaskService mmPublishJobSmsTaskService;

    @ApiOperation(value = "推送email")
    @PostMapping(value = "/email")
    public BaseResponse publishEmail(@RequestBody @Valid MmPublishJobEmailTaskV2DTO publishJobTaskDTO) throws IOException {
        mmPublishJobEmailTaskService.publishV2(publishJobTaskDTO);
        return BaseResponse.success();
    }

    @ApiOperation(value = "推送line")
    @PostMapping(value = "/line")
    public BaseResponse publishLine(@RequestBody @Valid MmPublishJobLineTaskV2DTO publishJobTaskDTO) throws IOException {
        mmPublishJobLineTaskService.publishV2(publishJobTaskDTO);
        return BaseResponse.success();
    }

    @ApiOperation(value = "推送sms")
    @PostMapping(value = "/sms")
    public BaseResponse publishSms(@RequestBody @Valid MmPublishJobSmsTaskV2DTO mmPublishJobSmsTaskDTO) throws IOException {
        mmPublishJobSmsTaskService.publishV2(mmPublishJobSmsTaskDTO);
        return BaseResponse.success();
    }

}
