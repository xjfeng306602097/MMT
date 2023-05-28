package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmActivityFile;
import com.makro.mall.admin.service.MmActivityFileService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 活动文件控制器
 * @date 2021/11/25
 */
@Api(tags = "MM活动文件接口")
@RestController
@RequestMapping("/api/v1/activity-file")
@RequiredArgsConstructor
public class MmActivityFileController {

    private final MmActivityFileService mmActivityFileService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmActivityFile>> list(@RequestBody SortPageRequest<MmActivityFile> request) {
        String sortSql = request.getSortSql();
        MmActivityFile req = request.getReq() != null ? request.getReq() : new MmActivityFile();
        IPage<MmActivityFile> files = mmActivityFileService.page(new MakroPage<>(request.getPage(), request.getLimit()),
                new LambdaQueryWrapper<MmActivityFile>().eq(StrUtil.isNotBlank(req.getMmCode()), MmActivityFile::getMmCode, req.getMmCode())
                        .eq(StrUtil.isNotBlank(req.getType()), MmActivityFile::getType, req.getType())
                        .last(StrUtil.isNotEmpty(sortSql), sortSql)
        );
        return BaseResponse.success(files);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmActivityFile> detail(@PathVariable Integer id) {
        return BaseResponse.success(mmActivityFileService.getById(id));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@Validated @RequestBody MmActivityFile file) {
        return BaseResponse.judge(mmActivityFileService.save(file));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
           @ApiParam("字典id") @PathVariable Long id,
           @ApiParam("实体JSON对象") @RequestBody MmActivityFile file) {
        file.setId(id);
        return BaseResponse.judge(mmActivityFileService.updateById(file));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        return BaseResponse.judge(mmActivityFileService.removeByIds(idList));
    }

}
