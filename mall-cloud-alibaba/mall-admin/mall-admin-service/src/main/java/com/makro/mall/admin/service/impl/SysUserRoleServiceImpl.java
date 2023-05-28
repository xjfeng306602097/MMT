package com.makro.mall.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.SysRoleMapper;
import com.makro.mall.admin.mapper.SysUserRoleMapper;
import com.makro.mall.admin.pojo.entity.SysRole;
import com.makro.mall.admin.pojo.entity.SysUserRole;
import com.makro.mall.admin.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 用户角色服务实现类
 * @date 2021/10/13
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<SysUserRole> listSysUserRoleByCode(List<String> roleCodes) {
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getCode, roleCodes));
        List<Long> roleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
        return this.list(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, roleIds));
    }


}
