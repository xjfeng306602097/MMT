package com.makro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.SysRoleMapper;
import com.makro.mall.admin.pojo.entity.SysRole;
import com.makro.mall.admin.pojo.entity.SysRoleMenu;
import com.makro.mall.admin.pojo.entity.SysRolePermission;
import com.makro.mall.admin.pojo.entity.SysUserRole;
import com.makro.mall.admin.service.SysRoleMenuService;
import com.makro.mall.admin.service.SysRolePermissionService;
import com.makro.mall.admin.service.SysRoleService;
import com.makro.mall.admin.service.SysUserRoleService;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description 用户角色服务
 * @date 2021/10/13
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysUserRoleService sysUserRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysRolePermissionService sysRolePermissionService;

    @Override
    @Transactional
    public Boolean delete(List<Long> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            long count = sysUserRoleService.count(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
            Assert.isTrue(count <= 0, StatusCode.TEMPLATE_NOT_EXISTS.args(id));
            sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
            sysRolePermissionService.remove(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
        });
        return this.removeByIds(ids);
    }

}




