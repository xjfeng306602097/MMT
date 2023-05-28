package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysPermission;

import java.util.List;


public interface SysPermissionService extends IService<SysPermission> {

    IPage<SysPermission> list(Page<SysPermission> page, SysPermission permission);

    List<String> listBtnPermByRoles(List<String> roles);

    Boolean refreshPermRolesRules();

    List<SysPermission> listPermRoles();
}
