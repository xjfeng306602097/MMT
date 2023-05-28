package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.file.pojo.entity.MmConfigUnit;
import com.makro.mall.file.service.MmConfigUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 单位控制器
 * @date 2021/10/26
 */
@Api(tags = "Unit接口")
@RestController
@RequestMapping("/api/v1/config/unit")
@RequiredArgsConstructor
public class MmConfigUnitController {

    private final MmConfigUnitService mmConfigUnitService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmConfigUnit>> pageList(@RequestBody SortPageRequest<MmConfigUnit> request) {
        String sortSql = request.getSortSql();
        MmConfigUnit req = request.getReq();
        LambdaQueryWrapper<MmConfigUnit> queryWrapper = new LambdaQueryWrapper<MmConfigUnit>()
                .eq(MmConfigUnit::getDeleted, 0)
                .eq(req.getStatus() != null, MmConfigUnit::getStatus, req.getStatus())
                .like(StrUtil.isNotBlank(req.getName()), MmConfigUnit::getName, req.getName())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<MmConfigUnit> result = mmConfigUnitService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "列表不分页")
    @GetMapping("/list")
    public BaseResponse<List<MmConfigUnit>> list() {
        return BaseResponse.success(mmConfigUnitService.list());
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmConfigUnit> detail(@PathVariable Long id) {
        return BaseResponse.success(mmConfigUnitService.getById(id));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@RequestBody MmConfigUnit type) {
        return BaseResponse.judge(mmConfigUnitService.save(type));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmConfigUnit type) {
        type.setId(id);
        return BaseResponse.judge(mmConfigUnitService.updateById(type));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmConfigUnitService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

}
