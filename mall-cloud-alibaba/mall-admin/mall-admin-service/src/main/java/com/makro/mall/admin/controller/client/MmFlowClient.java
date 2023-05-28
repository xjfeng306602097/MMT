package com.makro.mall.admin.controller.client;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.makro.mall.admin.pojo.entity.MmFlow;
import com.makro.mall.admin.service.MmFlowService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaojunfeng
 * @description MM Store
 * @date 2021/11/9
 */
@Api(tags = "MM Store接口")
@RestController
@RequestMapping("/api/v1/flow/client")
@RequiredArgsConstructor
public class MmFlowClient {

    private final MmFlowService mmFlowService;


    @ApiOperation(value = "回滚Flow")
    @PutMapping(value = "/rollback")
    public BaseResponse rollback(@RequestBody String code) {
        mmFlowService.update(new LambdaUpdateWrapper<MmFlow>()
                .in(MmFlow::getStatus, MmFlow.STATUS_NEW, MmFlow.STATUS_IN_PROGRESS_APPROVE)
                .eq(MmFlow::getCode, code)
                .set(MmFlow::getStatus, MmFlow.STATUS_IN_PROGRESS_CLOSED));
        return BaseResponse.success(true);
    }

}
