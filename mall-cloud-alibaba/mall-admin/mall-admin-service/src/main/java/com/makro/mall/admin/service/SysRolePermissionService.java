package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.RolePermissionDTO;
import com.makro.mall.admin.pojo.entity.SysRolePermission;

import java.util.List;


public interface SysRolePermissionService extends IService<SysRolePermission> {

    List<Long> listPermissionId(Long roleId);

    List<Long> listPermissionId(Long menuId, Long roleId);

    Boolean update(RolePermissionDTO rolePermission);
}
