package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.SysPermissionMapper;
import com.makro.mall.admin.mapper.SysRolePermissionMapper;
import com.makro.mall.admin.pojo.dto.RolePermissionDTO;
import com.makro.mall.admin.pojo.entity.SysPermission;
import com.makro.mall.admin.pojo.entity.SysRolePermission;
import com.makro.mall.admin.service.SysRolePermissionService;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author xiaojunfeng
 * @description 角色权限映射服务
 * @date 2021/10/13
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
        implements SysRolePermissionService {

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<Long> listPermissionId(Long roleId) {
        return this.baseMapper.listPermissionId(null, roleId);
    }

    @Override
    public List<Long> listPermissionId(Long menuId, Long roleId) {
        return this.baseMapper.listPermissionId(menuId, roleId);
    }

    @Override
    @Transactional
    public Boolean update(RolePermissionDTO rolePermission) {
        boolean result = true;
        List<Long> permissionIds = rolePermission.getPermissionIds();
        Long menuId = rolePermission.getMenuId();
        Long roleId = rolePermission.getRoleId();
        List<Long> dbPermissionIds = this.baseMapper.listPermissionId(menuId, roleId);
        // 判断是否存在对应menuId的permission
        List<SysPermission> sysPermissions = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getMenuId, rolePermission.getMenuId())
                .in(CollectionUtil.isNotEmpty(permissionIds), SysPermission::getId, permissionIds));
        Assert.isTrue(sysPermissions.size() > 0, AdminStatusCode.MENUID_PERMISSIONIDS_NOT_MATCH);
        if (rolePermission.getPermissionIds().isEmpty()) {
            permissionIds = new ArrayList<>();
        } else {
            permissionIds = sysPermissions.parallelStream().map(SysPermission::getId).collect(Collectors.toList());
        }
        // 删除数据库存在此次提交不存在的
        if (CollectionUtil.isNotEmpty(dbPermissionIds)) {
            List<Long> finalPermissionIds = permissionIds;
            List<Long> removePermissionIds = dbPermissionIds.stream().filter(id -> !finalPermissionIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(removePermissionIds)) {
                this.remove(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)
                        .in(SysRolePermission::getPermissionId, removePermissionIds));
            }
        }
        // 插入数据库不存在的
        if (CollectionUtil.isNotEmpty(permissionIds)) {
            List<Long> insertPermissionIds = permissionIds.stream().filter(id -> !dbPermissionIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(insertPermissionIds)) {
                List<SysRolePermission> roleMenus = new ArrayList<>();
                for (Long insertPermissionId : insertPermissionIds) {
                    SysRolePermission sysRolePermission = new SysRolePermission().setRoleId(roleId)
                            .setPermissionId(insertPermissionId);
                    roleMenus.add(sysRolePermission);
                }
                result = this.saveBatch(roleMenus);
            }
        }
        return result;
    }

}




