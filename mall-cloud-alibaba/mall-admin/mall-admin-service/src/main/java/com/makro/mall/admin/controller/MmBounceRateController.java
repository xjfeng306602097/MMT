package com.makro.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmBounceRate;
import com.makro.mall.admin.service.MmBounceRateService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/10/7
 */
@Api(tags = "MM-BounceRate(反弹率)")
@RestController
@RequestMapping("/api/v1/mm/bounceRate")
@RequiredArgsConstructor
public class MmBounceRateController {

    private final MmBounceRateService mmBounceRateService;

    @GetMapping("/{mmCode}")
    public BaseResponse<MmBounceRate> getByMmCode(@PathVariable String mmCode) {
        return BaseResponse.success(mmBounceRateService.getOne(new LambdaQueryWrapper<MmBounceRate>()
                .eq(MmBounceRate::getMmCode, mmCode)));
    }

}
