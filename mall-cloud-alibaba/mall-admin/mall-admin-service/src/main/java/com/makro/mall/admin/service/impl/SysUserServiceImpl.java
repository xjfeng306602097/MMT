package com.makro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.SysUserMapper;
import com.makro.mall.admin.pojo.dto.UserAuthDTO;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.admin.pojo.entity.SysUserRole;
import com.makro.mall.admin.service.SysUserRoleService;
import com.makro.mall.admin.service.SysUserService;
import com.makro.mall.common.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 用户服务实现类
 * @date 2021/10/13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleService sysUserRoleService;

    /**
     * 查询用户
     *
     * @param page
     * @param user
     * @return
     */
    @Override
    public IPage<SysUser> list(Page<SysUser> page, SysUser user) {
        List<SysUser> list = this.baseMapper.list(page, user);
        page.setRecords(list);
        return page;
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Boolean saveUser(SysUser user) {
        // 初始化用户 status=1,deleted=0
        user.init();
        user.setPassword(passwordEncoder.encode(GlobalConstants.DEFAULT_USER_PASSWORD));
        Boolean result = this.save(user);
        if (result) {
            List<Long> roleIds = user.getRoleIds();
            if (!CollectionUtils.isEmpty(roleIds)) {
                List<SysUserRole> userRoleList = new ArrayList<>();
                roleIds.forEach(roleId -> userRoleList.add(new SysUserRole().setUserId(user.getId()).setRoleId(roleId)));
                result = sysUserRoleService.saveBatch(userRoleList);
            }
        }
        return result;
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Boolean updateUser(SysUser user) {
        List<Long> oldRoleIds = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, user.getId())).stream()
                .map(SysUserRole::getRoleId).collect(Collectors.toList());
        // 新的用户角色ID集合
        List<Long> newRoleIds = user.getRoleIds();
        // 需要新增的用户角色ID集合
        List<Long> addRoleIds = newRoleIds.stream().filter(roleId -> !oldRoleIds.contains(roleId)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(addRoleIds)) {
            sysUserRoleService.saveBatch(addRoleIds.stream().map(r -> {
                return new SysUserRole().setUserId(user.getId()).setRoleId(r);
            }).collect(Collectors.toList()));
        }

        // 需要删除的用户的角色ID集合
        List<Long> removeRoleIds = oldRoleIds.stream().filter(roleId -> !newRoleIds.contains(roleId)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(removeRoleIds)) {
            removeRoleIds.forEach(roleId -> {
                sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()).eq(SysUserRole::getRoleId, roleId));
            });
        }
        // 更新用户
        return this.updateById(user);
    }

    /**
     * 根据账号名获取授权信息
     *
     * @param username
     * @return
     */
    @Override
    public UserAuthDTO getByUsername(String username) {
        return this.baseMapper.getByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(String password, SysUser user) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, user.getId());
        updateWrapper.set(SysUser::getPassword, passwordEncoder.encode(password));
        return this.update(updateWrapper);
    }

    @Override
    public SysUser detail(String id) {
        SysUser user = getById(id);
        if (user != null) {
            List<Long> roleIds = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, user.getId())
                    .select(SysUserRole::getRoleId)
            ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            user.setRoleIds(roleIds);
        }
        return user;
    }
}
