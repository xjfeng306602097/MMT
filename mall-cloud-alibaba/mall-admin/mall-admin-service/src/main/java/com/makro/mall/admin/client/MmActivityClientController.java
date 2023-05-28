package com.makro.mall.admin.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description MM活动
 * @date 2021/10/18
 */
@Api(tags = "MM活动接口")
@RestController
@RequestMapping("/api/v1/client/activity")
@RequiredArgsConstructor
public class MmActivityClientController {

    private final MmActivityService mmActivityService;

    @GetMapping(value = "/getPublishUrlByCode")
    public BaseResponse<MmActivity> getPublishUrlByCode(@RequestParam String mmCode) {
        return BaseResponse.success(mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().select(MmActivity::getPublishUrl, MmActivity::getStoreCode).eq(MmActivity::getMmCode, mmCode)));
    }

    @GetMapping(value = "/listMmCodeByStatus")
    public BaseResponse<List<MmActivity>> listMmCodeByStatus(@RequestParam Long status){
        return BaseResponse.success(mmActivityService.list(new LambdaQueryWrapper<MmActivity>().select(MmActivity::getMmCode).eq(MmActivity::getStatus, status)));
    }

}
