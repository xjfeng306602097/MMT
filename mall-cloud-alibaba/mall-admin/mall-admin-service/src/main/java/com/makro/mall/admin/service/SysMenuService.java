package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysMenu;
import com.makro.mall.admin.pojo.vo.MenuVO;
import com.makro.mall.admin.pojo.vo.MenuWithCheckVO;
import com.makro.mall.admin.pojo.vo.RouteVO;

import java.util.List;

/**
 *
 */
public interface SysMenuService extends IService<SysMenu> {

    List<MenuVO> listTable(String name, Boolean hasChild);

    List<RouteVO> listRoute();

    List<MenuWithCheckVO> listMenusWithChecked(List<Long> menuIds);
}
