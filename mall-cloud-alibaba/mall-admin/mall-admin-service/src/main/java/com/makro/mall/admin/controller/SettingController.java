package com.makro.mall.admin.controller;

import com.makro.mall.admin.pojo.entity.Setting;
import com.makro.mall.admin.repository.SettingRepository;
import com.makro.mall.admin.service.SettingService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags = "配置接口")
@RestController
@RequestMapping("/api/v1/setting")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @ApiOperation(value = "配置详情")
    @GetMapping
    public BaseResponse<Setting> detail() {
        return BaseResponse.success(settingService.findFirstById("1"));
    }

    @ApiOperation(value = "新增配置")
    @PostMapping
    public BaseResponse<Setting> add(@RequestBody Setting setting) {
        return BaseResponse.success(settingService.save(setting));
    }

    @ApiOperation(value = "修改配置")
    @PutMapping()
    public BaseResponse<Setting> update(@RequestBody Setting setting) {
        return BaseResponse.success(settingService.update(setting));
    }


}
