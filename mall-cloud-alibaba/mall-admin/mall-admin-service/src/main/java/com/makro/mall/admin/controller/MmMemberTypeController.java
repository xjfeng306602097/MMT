package com.makro.mall.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.service.MmMemberTypeService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description memberType
 * @date 2022/3/2
 */
@Api(tags = "MemberType接口")
@RestController
@RequestMapping("/api/v1/memberType")
@RequiredArgsConstructor
@Slf4j
public class MmMemberTypeController {

    private final MmMemberTypeService mmMemberTypeService;

    @ApiOperation(value = "memberType列表")
    @GetMapping("/list")
    public BaseResponse<List<MmMemberType>> list() {
        return BaseResponse.success(mmMemberTypeService.list());
    }

    @ApiOperation(value = "memberType分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmMemberType>> page(@RequestBody SortPageRequest<MmMemberType> request) {
        return BaseResponse.success(mmMemberTypeService.page(request));
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{id}")
    public BaseResponse page(@PathVariable String id, @RequestBody MmMemberType memberType) {
        memberType.setId(id);
        return BaseResponse.judge(mmMemberTypeService.updateById(memberType));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@Validated @RequestBody MmMemberType memberType) {
        return BaseResponse.judge(mmMemberTypeService.save(memberType));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable String id) {
        return BaseResponse.judge(mmMemberTypeService.removeById(id));
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse detail(@PathVariable String id) {
        return BaseResponse.success(mmMemberTypeService.getById(id));
    }

    @GetMapping("script")
    @ApiOperation(value = "将redis持久化到oracle")
    public BaseResponse<IPage<MmMemberType>> script() {
        mmMemberTypeService.script();
        return BaseResponse.success();
    }

}
