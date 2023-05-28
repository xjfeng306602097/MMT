package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysRole;

import java.util.List;

/**
 *
 */
public interface SysRoleService extends IService<SysRole> {

    Boolean delete(List<Long> ids);
}
