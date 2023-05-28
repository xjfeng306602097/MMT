package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.file.pojo.entity.MmConfigPreset;
import com.makro.mall.file.service.MmConfigPresetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 规格控制器
 * @date 2021/10/26
 */
@Api(tags = "Preset接口")
@RestController
@RequestMapping("/api/v1/config/preset")
@RequiredArgsConstructor
public class MmConfigPresetController {

    private final MmConfigPresetService mmConfigSizeService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmConfigPreset>> pageList(@RequestBody SortPageRequest<MmConfigPreset> request) {
        String sortSql = request.getSortSql();
        MmConfigPreset req = request.getReq();
        LambdaQueryWrapper<MmConfigPreset> queryWrapper = new LambdaQueryWrapper<MmConfigPreset>()
                .eq(MmConfigPreset::getDeleted, 0)
                .eq(req.getStatus() != null, MmConfigPreset::getStatus, req.getStatus())
                .like(StrUtil.isNotBlank(req.getName()), MmConfigPreset::getName, req.getName())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<MmConfigPreset> result = mmConfigSizeService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmConfigPreset> detail(@PathVariable Long id) {
        return BaseResponse.success(mmConfigSizeService.getById(id));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@RequestBody MmConfigPreset size) {
        if (size.getWidth() != null && size.getHeight() != null) {
            size.setSizeRate(size.getWidth().divide(size.getHeight(), 2, RoundingMode.HALF_UP));
        }
        return BaseResponse.judge(mmConfigSizeService.save(size));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmConfigPreset size) {
        if (size.getWidth() != null && size.getHeight() != null) {
            size.setSizeRate(size.getWidth().divide(size.getHeight(), 2, RoundingMode.HALF_UP));
        }
        size.setId(id);
        return BaseResponse.judge(mmConfigSizeService.updateById(size));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmConfigSizeService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

    @ApiOperation(value = "脚本,初始化sizeRate")
    @GetMapping("/initSizeRate")
    @Deprecated
    public BaseResponse<StatusCode> initSizeRate() {
        mmConfigSizeService.list().forEach(size -> {
            if (size.getWidth() != null && size.getHeight() != null) {
                size.setSizeRate(size.getWidth().divide(size.getHeight(), 2, RoundingMode.HALF_UP));
            }
            mmConfigSizeService.updateById(size);
        });
        return BaseResponse.success();
    }

}
