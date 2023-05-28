package com.makro.mall.admin.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmColor;
import com.makro.mall.admin.service.MmColorService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * @author Ljj
 */
@Api(tags = "color接口")
@RestController
@RequestMapping("/api/v1/color")
@Slf4j
@RequiredArgsConstructor
public class MmColorController {

    private final MmColorService mmColorService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmColor>> page(@RequestBody SortPageRequest<MmColor> request) {
        MmColor color = request.getReq();
        String sortSql = request.getSortSql();
        LambdaQueryWrapper<MmColor> wrapper = new LambdaQueryWrapper<MmColor>()
                .eq(StrUtil.isNotBlank(color.getRgb()), MmColor::getRgb, color.getRgb())
                .eq(StrUtil.isNotBlank(color.getCmyk()), MmColor::getCmyk, color.getCmyk())
                .eq(ObjectUtil.isNotNull(color.getStatus()), MmColor::getStatus, color.getStatus())
                .eq(ObjectUtil.isNotNull(color.getHex()), MmColor::getHex, color.getHex())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);

        return BaseResponse.success(mmColorService.page(new MakroPage<>(request.getPage(), request.getLimit()), wrapper));
    }


    @ApiOperation(value = "color详情")
    @GetMapping("/{id}")
    public BaseResponse<MmColor> detail(@PathVariable String id) {
        return BaseResponse.success(mmColorService.getById(id));
    }

    @ApiOperation(value = "新增color")
    @PostMapping
    public BaseResponse add(@Validated @RequestBody MmColor user) {
        return BaseResponse.judge(mmColorService.save(user));
    }

    @ApiOperation(value = "修改color")
    @PutMapping(value = "/{id}")
    public BaseResponse update(@PathVariable Long id, @RequestBody MmColor color) {
        color.setId(id);
        return BaseResponse.judge(mmColorService.updateById(color));
    }

    @ApiOperation(value = "删除color")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable @ApiParam("id ,分割") String ids) {
        return BaseResponse.judge(mmColorService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList())));
    }


}
