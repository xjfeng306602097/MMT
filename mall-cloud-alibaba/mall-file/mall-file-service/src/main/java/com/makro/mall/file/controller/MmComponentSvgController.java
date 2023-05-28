package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.file.pojo.entity.MmComponentSvg;
import com.makro.mall.file.service.MmComponentSvgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description svg维护接口
 * @date 2021/10/26
 */
@Api(tags = "svg维护接口")
@RestController
@RequestMapping("/api/v1/component/svg")
@RequiredArgsConstructor
public class MmComponentSvgController {

    private final MmComponentSvgService mmComponentSvgService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmComponentSvg>> pageList(@RequestBody SortPageRequest<MmComponentSvg> request) {
        String sortSql = request.getSortSql();
        LambdaQueryWrapper<MmComponentSvg> queryWrapper = new LambdaQueryWrapper<MmComponentSvg>()
                .eq(MmComponentSvg::getDeleted, 0)
                .eq(request.getReq().getStatus() != null, MmComponentSvg::getStatus, request.getReq().getStatus())
                .like(StrUtil.isNotBlank(request.getReq().getName()), MmComponentSvg::getName, request.getReq().getName())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<MmComponentSvg> result = mmComponentSvgService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "svg详情")
    @GetMapping("/{id}")
    public BaseResponse<MmComponentSvg> detail(@PathVariable Long id) {
        return BaseResponse.success(mmComponentSvgService.getById(id));
    }

    @ApiOperation(value = "新增svg")
    @PostMapping
    public BaseResponse add(@RequestBody MmComponentSvg svg) {
        return BaseResponse.judge(mmComponentSvgService.save(svg));
    }

    @ApiOperation(value = "修改svg")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmComponentSvg svg) {
        svg.setId(id);
        return BaseResponse.judge(mmComponentSvgService.updateById(svg));
    }

    @ApiOperation(value = "删除svg")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmComponentSvgService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList())));
    }

}
