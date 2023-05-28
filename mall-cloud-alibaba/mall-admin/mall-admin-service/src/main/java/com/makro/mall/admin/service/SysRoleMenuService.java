package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysRoleMenu;

import java.util.List;

/**
 *
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    List<Long> listMenuIds(Long roleId);

    Boolean update(Long roleId, List<Long> menuIds);

}
