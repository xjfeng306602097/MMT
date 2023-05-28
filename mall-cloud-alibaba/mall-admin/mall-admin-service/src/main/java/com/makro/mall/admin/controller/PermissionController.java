package com.makro.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.aspect.annotation.RefreshPermRolesRules;
import com.makro.mall.admin.pojo.entity.SysPermission;
import com.makro.mall.admin.service.SysPermissionService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * @author xiaojunfeng
 * @description 权限处理
 * @date 2021/10/19
 */
@Api(tags = "权限接口")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@RefreshPermRolesRules
public class PermissionController {

    private final SysPermissionService sysPermissionService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<IPage<SysPermission>> pageList(@ApiParam("页码") Integer page,
                                                       @ApiParam("每页数量") Integer limit,
                                                       @ApiParam("权限名称") String name,
                                                       @ApiParam("菜单ID") Long menuId,
                                                       @ApiParam("按钮权限") String btnPerm,
                                                       @ApiParam("url表达式") String urlPerm) {
        SysPermission sysPermission = new SysPermission().setMenuId(menuId).setName(name).setBtnPerm(btnPerm).setUrlPerm(urlPerm);
        return BaseResponse.success(sysPermissionService.list(new MakroPage<>(page, limit), sysPermission));
    }


    @ApiOperation(value = "权限列表")
    @GetMapping
    public BaseResponse<List<SysPermission>> list(@ApiParam("菜单ID") Long menuId) {
        List<SysPermission> list = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>()
                .eq(menuId != null, SysPermission::getMenuId, menuId));
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "权限详情")
    @GetMapping("/{id}")
    public BaseResponse<SysPermission> detail(@ApiParam("权限ID") @PathVariable Long id) {
        SysPermission permission = sysPermissionService.getById(id);
        return BaseResponse.success(permission);
    }

    @ApiOperation(value = "新增权限")
    @PostMapping
    public BaseResponse add(@ApiParam("实体JSON对象") @RequestBody SysPermission permission) {
        return BaseResponse.judge(sysPermissionService.save(permission));
    }

    @ApiOperation(value = "修改权限")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @ApiParam("权限id") @PathVariable Long id,
            @ApiParam("实体JSON对象") @RequestBody SysPermission permission) {
        permission.setId(id);
        return BaseResponse.judge(sysPermissionService.updateById(permission));
    }

    @ApiOperation(value = "删除权限")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("id集合") @PathVariable String ids) {
        return BaseResponse.judge(sysPermissionService.removeByIds(Arrays.asList(ids.split(","))));
    }
}
