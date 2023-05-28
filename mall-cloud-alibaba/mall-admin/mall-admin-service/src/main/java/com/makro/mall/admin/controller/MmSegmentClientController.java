package com.makro.mall.admin.controller;

import com.makro.mall.admin.service.MmSegmentService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 卢嘉俊
 * @description SegmentAPI接口
 * @date 2022/5/16
 */
@RestController
@RequestMapping("/api/v1/segments")
@Slf4j
@RequiredArgsConstructor
@Api(hidden = true)
public class MmSegmentClientController {

    private final MmSegmentService mmSegmentService;


    @ApiOperation(value = "处理失效Segment", hidden = true)
    @GetMapping(value = "/invalidSegmentHandler")
    public BaseResponse<Boolean> invalidSegmentHandler() {
        return BaseResponse.success(mmSegmentService.invalidSegmentHandler());
    }

    @ApiOperation(value = "获取SegmentId如果没有则创建", hidden = true)
    @GetMapping(value = "/getIdIfNullCreateThat")
    BaseResponse<Long> getIdIfNullCreateThat(@RequestParam String segmentName) {
        return BaseResponse.success(mmSegmentService.getIdIfNullCreateThat(segmentName));
    }

}
