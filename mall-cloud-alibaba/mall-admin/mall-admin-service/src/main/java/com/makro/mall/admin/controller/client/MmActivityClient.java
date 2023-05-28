package com.makro.mall.admin.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaojunfeng
 * @description MM活动
 * @date 2021/10/18
 */
@Api(tags = "MM活动接口", hidden = true)
@RestController
@RequestMapping("/api/v1/activity/client")
@RequiredArgsConstructor
public class MmActivityClient {

    private final MmActivityService mmActivityService;


    @ApiOperation(value = "活动详情-根据code获取", hidden = true)
    @GetMapping("/mmCode/{mmCode}")
    public BaseResponse<MmActivity> getVOByCode(@PathVariable String mmCode) {
        return BaseResponse.success(mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, mmCode)));
    }

}
