package com.makro.mall.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleService extends IService<SysUserRole> {
    List<SysUserRole> listSysUserRoleByCode(List<String> roleCodes);
}
