package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.aspect.annotation.RefreshPermRolesRules;
import com.makro.mall.admin.pojo.entity.SysMenu;
import com.makro.mall.admin.pojo.vo.MenuVO;
import com.makro.mall.admin.pojo.vo.RouteVO;
import com.makro.mall.admin.service.SysMenuService;
import com.makro.mall.admin.service.SysPermissionService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author xiaojunfeng
 * @description 菜单管理
 * @date 2021/10/19
 */
@Api(tags = "菜单接口")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Slf4j
@RefreshPermRolesRules
public class MenuController {

    private final SysMenuService menuService;

    @ApiOperation(value = "菜单表格（Table）层级列表")
    @GetMapping("/table")
    public BaseResponse getTableList(@ApiParam("菜单名称") String name,@ApiParam("是否展开层级")  Boolean hasChild) {
        List<MenuVO> menuList = menuService.listTable(name, hasChild);
        return BaseResponse.success(menuList);
    }

    @ApiOperation(value = "菜单路由（Route）层级列表")
    @GetMapping("/route")
    public BaseResponse getRouteList() {
        List<RouteVO> routeList = menuService.listRoute();
        return BaseResponse.success(routeList);
    }

    @ApiOperation(value = "菜单列表分页")
    @GetMapping
    public BaseResponse<IPage<SysMenu>> list(Integer page, Integer limit,@ApiParam("菜单名称") String name) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<SysMenu>()
                .like(StrUtil.isNotBlank(name), SysMenu::getName, name)
                .orderByAsc(SysMenu::getSort)
                .orderByDesc(SysMenu::getGmtModified)
                .orderByDesc(SysMenu::getGmtCreate);
        return BaseResponse.success(menuService.page(new MakroPage<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "菜单详情")
    @GetMapping("/{id}")
    public BaseResponse detail(@PathVariable Integer id) {
        return BaseResponse.success(menuService.getById(id));
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping
    @CacheEvict(cacheNames = "system", key = "'routeList'")
    public BaseResponse add(@RequestBody SysMenu menu) {
        return BaseResponse.judge(menuService.save(menu));
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping(value = "/{id}")
    @CacheEvict(cacheNames = "system", key = "'routeList'")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody SysMenu menu) {
        return BaseResponse.judge(menuService.updateById(menu));
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/{ids}")
    @CacheEvict(cacheNames = "system", key = "'routeList'")
    public BaseResponse delete(@PathVariable("ids") String ids) {
        return BaseResponse.judge(menuService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList())));
    }

    @ApiOperation(value = "选择性修改菜单")
    @PatchMapping(value = "/{id}")
    @CacheEvict(cacheNames = "system", key = "'routeList'")
    public BaseResponse patch(@PathVariable Integer id, @RequestBody SysMenu menu) {
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<SysMenu>().eq(SysMenu::getId, id);
        updateWrapper.set(menu.getVisible() != null, SysMenu::getVisible, menu.getVisible());
        return BaseResponse.judge(menuService.update(updateWrapper));
    }
}
