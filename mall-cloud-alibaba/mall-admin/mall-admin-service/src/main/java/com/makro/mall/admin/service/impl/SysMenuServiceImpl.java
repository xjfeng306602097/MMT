package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.common.constant.SystemConstants;
import com.makro.mall.admin.mapper.SysMenuMapper;
import com.makro.mall.admin.pojo.entity.SysMenu;
import com.makro.mall.admin.pojo.vo.MenuVO;
import com.makro.mall.admin.pojo.vo.MenuWithCheckVO;
import com.makro.mall.admin.pojo.vo.RouteVO;
import com.makro.mall.admin.service.SysMenuService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    /**
     * 菜单表格（Table）层级列表
     *
     * @param name 菜单名称
     * @return
     */
    @Override
    public List<MenuVO> listTable(String name, Boolean hasChild) {
        List<SysMenu> menuList = this.list(
                new LambdaQueryWrapper<SysMenu>()
                        .like(StrUtil.isNotBlank(name), SysMenu::getName, name)
                        .orderByAsc(SysMenu::getSort)
        );
        hasChild = Optional.ofNullable(hasChild).orElse(Boolean.TRUE);
        if (hasChild) {
            return recursionTableList(SystemConstants.ROOT_MENU_ID, menuList);
        } else {
            return menuList.stream().map(m -> BeanUtil.copyProperties(m, MenuVO.class)).collect(Collectors.toList());
        }
    }

    @Override
    @Cacheable(cacheNames = "system", key = "'routeList'", sync = true)
    public List<RouteVO> listRoute() {
        List<SysMenu> menuList = this.baseMapper.listRoute();
        return recursionRoute(SystemConstants.ROOT_MENU_ID, menuList);
    }

    @Override
    public List<MenuWithCheckVO> listMenusWithChecked(List<Long> menuIds) {
        List<SysMenu> menuList = this.list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort));
        return recursionChecked(SystemConstants.ROOT_MENU_ID, menuList, menuIds);
    }

    private List<MenuWithCheckVO> recursionChecked(Long parentId, List<SysMenu> menuList, List<Long> menuIds) {
        List<MenuWithCheckVO> menuTableList = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .forEach(menu -> {
                    MenuWithCheckVO menuVO = new MenuWithCheckVO();
                    BeanUtil.copyProperties(menu, menuVO);
                    List<MenuWithCheckVO> children = recursionChecked(menu.getId(), menuList, menuIds);
                    if (CollectionUtil.isNotEmpty(children)) {
                        menuVO.setChildren(children);
                    }
                    if (menuIds.contains(menuVO.getId())) {
                        menuVO.setChecked(true);
                    }
                    menuTableList.add(menuVO);
                });
        return menuTableList;
    }

    /**
     * 递归生成菜单表格层级列表
     *
     * @param parentId 父级ID
     * @param menuList 菜单列表
     * @return
     */
    private static List<MenuVO> recursionTableList(Long parentId, List<SysMenu> menuList) {
        List<MenuVO> menuTableList = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .forEach(menu -> {
                    MenuVO menuVO = new MenuVO<>();
                    BeanUtil.copyProperties(menu, menuVO);
                    List<MenuVO> children = recursionTableList(menu.getId(), menuList);
                    if (CollectionUtil.isNotEmpty(children)) {
                        menuVO.setChildren(children);
                    }
                    menuTableList.add(menuVO);
                });
        return menuTableList;
    }

    /**
     * 递归生成菜单路由层级列表
     *
     * @param parentId 父级ID
     * @param menuList 菜单列表
     * @return
     */
    private List<RouteVO> recursionRoute(Long parentId, List<SysMenu> menuList) {
        List<RouteVO> list = new ArrayList<>();
        Optional.ofNullable(menuList).ifPresent(menus -> menus.stream().filter(menu -> menu.getParentId().equals(parentId))
                .forEach(menu -> {
                    RouteVO routeVO = new RouteVO();
                    routeVO.setName(menu.getId() + "");
                    // 根据path路由跳转 this.$router.push({name:xxx})
                    routeVO.setPath(menu.getPath());
                    routeVO.setRedirect(menu.getRedirect());
                    routeVO.setComponent(menu.getComponent());
                    RouteVO.Meta meta = new RouteVO.Meta(menu.getName(), menu.getIcon(), menu.getRoles());
                    routeVO.setMeta(meta);
                    // 菜单显示隐藏
                    routeVO.setHidden(ObjectUtil.equal(0L, menu.getVisible()));
                    List<RouteVO> children = recursionRoute(menu.getId(), menuList);
                    routeVO.setChildren(children);
                    list.add(routeVO);
                }));
        return list;

    }

}




