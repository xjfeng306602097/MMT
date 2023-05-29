package com.makro.mall.admin.client;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public BaseResponse<List<MmActivity>> listMmCodeByStatus(@RequestParam Long status) {
        return BaseResponse.success(mmActivityService.list(new LambdaQueryWrapper<MmActivity>().select(MmActivity::getMmCode).eq(MmActivity::getStatus, status)));
    }

    @PostMapping(value = "/getNameByCodes")
    public BaseResponse<Map<String, String>> getNameByCodes(@RequestBody List<String> mmCodes) {
        List<MmActivity> list = mmActivityService.list(new LambdaQueryWrapper<MmActivity>()
                .select(MmActivity::getMmCode, MmActivity::getTitle)
                .in(MmActivity::getMmCode, mmCodes));
        if (CollUtil.isEmpty(list)) {
            return BaseResponse.success(new HashMap<>());
        }
        Map<String, String> map = list.stream().collect(Collectors.toMap(MmActivity::getMmCode, MmActivity::getTitle));
        return BaseResponse.success(map);
    }

}
