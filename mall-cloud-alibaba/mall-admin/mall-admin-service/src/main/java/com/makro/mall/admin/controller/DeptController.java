package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.SysDept;
import com.makro.mall.admin.pojo.vo.DeptVO;
import com.makro.mall.admin.pojo.vo.TreeSelectVO;
import com.makro.mall.admin.service.SysDeptService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 部门
 * @date 2021/10/18
 */
@Api(tags = "部门接口")
@RestController
@RequestMapping("/api/v1/depts")
@RequiredArgsConstructor
public class DeptController {

    private final SysDeptService sysDeptService;

    @ApiOperation(value = "部门表格（Table）层级列表")
    @GetMapping("/table")
    public BaseResponse getTableList(@ApiParam("部门状态") Integer status,
                                     @ApiParam("部门名称") String name,
                                     @ApiParam("是否展开层级") boolean hasChild) {
        return BaseResponse.success(sysDeptService.listTable(status, name, hasChild));
    }

    @ApiOperation(value = "部门下拉（TreeSelect）层级列表")
    @GetMapping("/select")
    public BaseResponse getSelectList() {
        return BaseResponse.success(sysDeptService.listTreeSelect());
    }

    @ApiOperation(value = "部门列表分页")
    @GetMapping
    public BaseResponse<IPage<SysDept>> list(@ApiParam("页码") Integer page,
                                             @ApiParam("每页数量") Integer limit,
                                             @ApiParam("菜单名称") String name) {
        return BaseResponse.success(sysDeptService.page(new MakroPage<>(page, limit), new LambdaQueryWrapper<SysDept>()
                .like(StrUtil.isNotBlank(name), SysDept::getName, name)
                .orderByAsc(SysDept::getSort)
                .orderByDesc(SysDept::getGmtModified)
                .orderByDesc(SysDept::getGmtCreate)));
    }

    @ApiOperation(value = "部门详情")
    @GetMapping("/{id}")
    public BaseResponse detail(@ApiParam("部门id") @PathVariable Long id) {
        SysDept sysDept = sysDeptService.getById(id);
        return BaseResponse.success(sysDept);
    }

    @ApiOperation(value = "新增部门")
    @PostMapping
    public BaseResponse add(@RequestBody SysDept dept) {
        Long id = sysDeptService.saveDept(dept);
        return BaseResponse.success(id);
    }

    @ApiOperation(value = "修改部门")
    @PutMapping(value = "/{id}")
    public BaseResponse update(@PathVariable Long id, @RequestBody SysDept dept) {
        dept.setId(id);
        Long deptId = sysDeptService.saveDept(dept);
        return BaseResponse.success(deptId);
    }

    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("部门ID，多个以英文逗号,分割拼接") @PathVariable("ids") String ids) {
        return BaseResponse.judge(sysDeptService.deleteByIds(ids));
    }

}
