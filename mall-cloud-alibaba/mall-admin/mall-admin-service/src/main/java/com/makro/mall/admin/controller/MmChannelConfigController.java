package com.makro.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmChannelConfig;
import com.makro.mall.admin.service.MmChannelConfigService;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/11/1
 */
@Api(tags = "渠道配置")
@RestController
@RequestMapping("/api/v1/mm/channel/config")
@RequiredArgsConstructor
public class MmChannelConfigController {

    private final MmChannelConfigService mmChannelConfigService;

    @ApiOperation(value = "根据id获取配置详情")
    @GetMapping("/{id}")
    public BaseResponse<MmChannelConfig> detail(@PathVariable Long id) {
        return BaseResponse.success(mmChannelConfigService.getById(id));
    }


    @ApiOperation(value = "根据准确名称获取配置详情")
    @GetMapping("/name/{name}")
    public BaseResponse<MmChannelConfig> getByName(@PathVariable String name) {
        return BaseResponse.success(mmChannelConfigService.getOne(
                new LambdaQueryWrapper<MmChannelConfig>().eq(MmChannelConfig::getChannelName, name)));
    }

    @ApiOperation(value = "新增渠道配置")
    @PostMapping
    public BaseResponse add(@RequestBody MmChannelConfig config) {
        MmChannelConfig dbConfig = mmChannelConfigService.getOne(
                new LambdaQueryWrapper<MmChannelConfig>().eq(MmChannelConfig::getChannelName, config.getChannelName()));
        Assert.isTrue(dbConfig == null, AdminStatusCode.USER_NAME_DUPLICATE);
        return BaseResponse.judge(mmChannelConfigService.save(config));
    }


    @ApiOperation(value = "修改配置")
    @PutMapping(value = "/{id}")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmChannelConfig config) {
        config.setId(id);
        return BaseResponse.judge(mmChannelConfigService.updateById(config));
    }

    @ApiOperation(value = "删除流程配置节点")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return BaseResponse.judge(mmChannelConfigService.removeByIds(idList));
    }

}
