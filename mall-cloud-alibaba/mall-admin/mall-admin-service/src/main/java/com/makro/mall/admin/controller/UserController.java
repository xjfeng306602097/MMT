package com.makro.mall.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.dto.UserAuthDTO;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.admin.pojo.vo.RouteVO;
import com.makro.mall.admin.pojo.vo.UserVO;
import com.makro.mall.admin.service.SysMenuService;
import com.makro.mall.admin.service.SysPermissionService;
import com.makro.mall.admin.service.SysUserService;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.mybatis.wrapper.MarkoLambdaUpdateWrapper;
import com.makro.mall.common.web.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 用户API接口
 * @date 2021/10/13
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;
    private final SysPermissionService sysPermissionService;
    private final PasswordEncoder passwordEncoder;
    private final SysMenuService sysMenuService;

    @ApiOperation(value = "列表分页")
    @GetMapping
    public BaseResponse<IPage<SysUser>> list(@ApiParam("页码") Integer page,
                                             @ApiParam("每页数量") Integer limit,
                                             @ApiParam("用户昵称") String nickname,
                                             @ApiParam("手机号码") String phone,
                                             @ApiParam("状态") Integer status,
                                             @ApiParam("部门ID") Long deptId,
                                             @ApiParam("角色ID,用逗号分隔") String roles,
                                             @ApiParam("起始日期") String lastLoginTimeStart,
                                             @ApiParam("终止日期") String lastLoginTimeEnd) {
        SysUser user = new SysUser();
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setStatus(status);
        user.setDeptId(deptId);
        user.setLastLoginTimeStart(lastLoginTimeStart);
        user.setLastLoginTimeEnd(lastLoginTimeEnd);
        if (StrUtil.isNotEmpty(roles)) {
            user.setRoleIds(Arrays.stream(roles.split(","))
                    .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
        }
        IPage<SysUser> result = sysUserService.list(new MakroPage<>(page, limit), user);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "用户详情")
    @GetMapping("/{id}")
    public BaseResponse<SysUser> detail(@ApiParam("用户ID") @PathVariable String id) {
        SysUser user = sysUserService.detail(id);
        return BaseResponse.success(user);
    }


    @ApiOperation(value = "新增用户")
    @PostMapping
    public BaseResponse add(@Validated @RequestBody SysUser user) {
        user.setId(IdUtil.randomUUID());
        UserAuthDTO dbUser = sysUserService.getByUsername(user.getUsername());
        Assert.isTrue(dbUser == null, StatusCode.USER_NAME_DUPLICATE);
        Boolean result = sysUserService.saveUser(user);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "修改用户")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @ApiParam("用户ID") @PathVariable String id,
            @RequestBody SysUser user) {
        user.setId(id);
        if (StrUtil.isNotEmpty(user.getUsername())) {
            return BaseResponse.error(StatusCode.ILLEGAL_STATE);
        }
        Boolean result = sysUserService.updateUser(user);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("id集合") @PathVariable String ids) {
        Boolean status = sysUserService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "选择性更新用户")
    @PatchMapping(value = "/{id}")
    public BaseResponse patch(@ApiParam("用户ID") @PathVariable String id, @RequestBody SysUser user) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, id);
        updateWrapper.set(user.getStatus() != null, SysUser::getStatus, user.getStatus());
        if (StrUtil.isNotEmpty(user.getPassword())) {
            updateWrapper.set(SysUser::getPassword, passwordEncoder.encode(user.getPassword()));
        }
        return BaseResponse.judge(sysUserService.update(updateWrapper));
    }

    @ApiOperation(value = "更新登录信息")
    @PutMapping(value = "/{id}/login/info")
    public BaseResponse loginInfo(@ApiParam("用户ID") @PathVariable String id, @RequestBody SysUser user) {
        MarkoLambdaUpdateWrapper<SysUser> updateWrapper = (MarkoLambdaUpdateWrapper<SysUser>) new MarkoLambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, id);
        updateWrapper.set(SysUser::getLastLoginTime, user.getLastLoginTime());
        updateWrapper.set(SysUser::getLastLoginIp, user.getLastLoginIp());
        updateWrapper.incrField(SysUser::getLoginNum, 1);
        return BaseResponse.judge(sysUserService.update(updateWrapper));
    }

    /**
     * 提供用于用户登录认证信息
     */
    @ApiOperation(value = "根据用户名获取用户信息")
    @GetMapping("/username/{username}")
    public BaseResponse<UserAuthDTO> getUserByUsername(@ApiParam("用户名") @PathVariable String username) {
        UserAuthDTO user = sysUserService.getByUsername(username);
        return BaseResponse.success(user);
    }

    @ApiOperation(value = "获取当前登陆的用户信息")
    @GetMapping("/me")
    public BaseResponse<UserVO> getCurrentUser() {
        UserVO userVO = new UserVO();
        // 用户基本信息
        String userId = JwtUtils.getUserId();
        SysUser user = sysUserService.getById(userId);
        BeanUtil.copyProperties(user, userVO);
        // 用户角色信息
        List<String> roles = JwtUtils.getRoles();
        userVO.setRoles(roles);
        // 用户按钮权限信息
        if (CollectionUtil.isNotEmpty(roles)) {
            List<String> perms = sysPermissionService.listBtnPermByRoles(roles);
            userVO.setPerms(perms);
        }
        // 返回用户VO
        return BaseResponse.success(userVO);
    }

    @ApiOperation(value = "获取当前登陆的用户可访问的菜单")
    @GetMapping("/menus")
    public BaseResponse menus() {
        // 用户角色信息
        List<String> roles = JwtUtils.getRoles();
        List<RouteVO> routeVOS = sysMenuService.listRoute();
        // 递归过滤没有权限的菜单
        recursionRoute(routeVOS, roles);
        return BaseResponse.success(routeVOS);
    }

    /**
     * 递归去除没有权限的菜单
     *
     * @param routeVOS
     * @param roles
     */
    private void recursionRoute(List<RouteVO> routeVOS, List<String> roles) {
        Iterator<RouteVO> iterator = routeVOS.iterator();
        while (iterator.hasNext()) {
            RouteVO routeVO = iterator.next();
            if (Collections.disjoint(routeVO.getMeta().getRoles(), roles)) {
                iterator.remove();
            }
            if (CollectionUtil.isNotEmpty(routeVO.getChildren())) {
                recursionRoute(routeVO.getChildren(), roles);
            }
        }
    }

}
