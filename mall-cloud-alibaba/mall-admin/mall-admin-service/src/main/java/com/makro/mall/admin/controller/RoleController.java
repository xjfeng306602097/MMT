package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.aspect.annotation.RefreshPermRolesRules;
import com.makro.mall.admin.pojo.dto.RolePermissionDTO;
import com.makro.mall.admin.pojo.entity.SysRole;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.constants.GlobalConstants;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.web.util.JwtUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 角色处理
 * @date 2021/10/19
 */
@Api(tags = "角色处理相关")
@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
@RefreshPermRolesRules
public class RoleController {

    private final SysRoleService sysRoleService;
    private final SysRolePermissionService sysRolePermissionService;
    private final SysPermissionService sysPermissionService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysMenuService sysMenuService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<IPage<SysRole>> pageList(@ApiParam("页码") Integer page,
                                                 @ApiParam("每页数量") Integer limit,
                                                 @ApiParam("角色名称") String name) {
        List<String> roles = JwtUtils.getRoles();
        // 判断是否是超级管理员
        boolean isRoot = Optional.ofNullable(roles).orElse(new ArrayList<>())
                .contains(GlobalConstants.ROOT_ROLE_CODE);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>()
                .like(StrUtil.isNotBlank(name), SysRole::getName, name)
                .ne(!isRoot, SysRole::getCode, GlobalConstants.ROOT_ROLE_CODE)
                .orderByAsc(SysRole::getSort)
                .orderByDesc(SysRole::getGmtModified)
                .orderByDesc(SysRole::getGmtCreate);
        IPage<SysRole> result = sysRoleService.page(new MakroPage<>(page, limit), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "角色列表")
    @GetMapping
    public BaseResponse list() {
        List<String> roles = JwtUtils.getRoles();
        // 判断是否是超级管理员
        boolean isRoot = Optional.ofNullable(roles).orElse(new ArrayList<>())
                .contains(GlobalConstants.ROOT_ROLE_CODE);
        List<SysRole> list = sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, GlobalConstants.STATUS_YES)
                .ne(!isRoot, SysRole::getCode, GlobalConstants.ROOT_ROLE_CODE)
                .orderByAsc(SysRole::getSort)
        );
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "新增角色")
    @PostMapping
    public BaseResponse add(@RequestBody SysRole sysRole) {
        long count = sysRoleService.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, sysRole.getCode())
                .or().eq(SysRole::getName, sysRole.getName()));
        Assert.isTrue(count == 0, AdminStatusCode.CURRENT_ROLE_NAME_OR_CODE_DUPLICATE);
        return BaseResponse.judge(sysRoleService.save(sysRole));
    }

    @ApiOperation(value = "修改角色")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
           @ApiParam("角色id") @PathVariable Long id,
           @ApiParam("实体JSON对象") @RequestBody SysRole role) {
        long count = sysRoleService.count(new LambdaQueryWrapper<SysRole>()
                .and(wrapper -> wrapper.eq(SysRole::getCode, role.getCode())
                        .or()
                        .eq(SysRole::getName, role.getName()))
                .ne(SysRole::getId, id)
        );
        Assert.isTrue(count == 0, AdminStatusCode.CURRENT_ROLE_NAME_OR_CODE_DUPLICATE);
        role.setId(id);
        return BaseResponse.judge(sysRoleService.updateById(role), "no.changes.happens");
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(sysRoleService.delete(Arrays.stream(ids.split(","))
                .map(Long::parseLong).collect(Collectors.toList())));
    }

    @ApiOperation(value = "选择性修改角色")
    @PatchMapping(value = "/{id}")
    public BaseResponse patch(@ApiParam("用户Id") @PathVariable Long id, @RequestBody SysRole role) {
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getId, id)
                .set(role.getStatus() != null, SysRole::getStatus, role.getStatus());

        return BaseResponse.judge(sysRoleService.update(updateWrapper));
    }

    @ApiOperation(value = "获取角色拥有的菜单ID集合")
    @GetMapping("/{id}/menus")
    public BaseResponse listRoleMenu(@PathVariable("id") Long roleId) {
        return BaseResponse.success(sysRoleMenuService.listMenuIds(roleId));
    }

    @ApiOperation(value = "获取所有菜单，并列出哪些菜单是用户所选的")
    @GetMapping("/{id}/menus-with-check")
    public BaseResponse listMenusWithChecked(@PathVariable("id") Long roleId) {
        return BaseResponse.success(sysMenuService.listMenusWithChecked(sysRoleMenuService.listMenuIds(roleId)));
    }


    @ApiOperation(value = "获取角色拥有的权限ID集合")
    @GetMapping("/{id}/permissions")
    public BaseResponse listRolePermission(@PathVariable("id") Long roleId, Long menuId) {
        return BaseResponse.success(sysRolePermissionService.listPermissionId(menuId, roleId));
    }

    @ApiOperation(value = "修改角色菜单")
    @PutMapping(value = "/{id}/menus")
    public BaseResponse updateRoleMenu(
            @PathVariable("id") Long roleId,
            @RequestBody SysRole role) {
        List<Long> menuIds = role.getMenuIds();
        return BaseResponse.judge(sysRoleMenuService.update(roleId, menuIds));
    }

    @ApiOperation(value = "修改角色权限")
    @PutMapping(value = "/{id}/permissions")
    public BaseResponse updateRolePermission(
            @PathVariable("id") Long roleId,
            @RequestBody RolePermissionDTO rolePermission) {
        rolePermission.setRoleId(roleId);
        return BaseResponse.judge(sysRolePermissionService.update(rolePermission));
    }

    @ApiOperation(value = "获取角色详情")
    @GetMapping(value = "/{id}")
    public BaseResponse<SysRole> detail(@PathVariable("id") Long id) {
        return BaseResponse.success(sysRoleService.getById(id));
    }

}
