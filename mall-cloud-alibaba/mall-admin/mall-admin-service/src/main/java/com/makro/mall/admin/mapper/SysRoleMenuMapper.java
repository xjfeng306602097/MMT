package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.admin.pojo.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.makro.mall.admin.pojo.entity.SysRoleMenu
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    @Select(" SELECT " +
            " 	t1.menu_id  " +
            " FROM " +
            " 	sys_role_menu t1 " +
            " 	INNER JOIN sys_menu t2 ON t1.menu_id = t2.id  " +
            " WHERE role_id =#{roleId}")
    List<Long> listMenuIds(Long roleId);
}




