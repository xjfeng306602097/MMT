package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.admin.pojo.entity.SysPermission
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission> listPermRoles();

    List<String> listBtnPermByRoles(List<String> roles);

    List<SysPermission> list(Page<SysPermission> page, SysPermission permission);
}




