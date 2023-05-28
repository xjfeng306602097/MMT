package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.UserAuthDTO;
import com.makro.mall.admin.pojo.entity.SysUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaojunfeng
 * @description 用户服务
 * @date 2021/10/13
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户分页列表
     *
     * @param page
     * @param user
     * @return
     */
    IPage<SysUser> list(Page<SysUser> page, SysUser user);

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    Boolean saveUser(SysUser user);

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    Boolean updateUser(SysUser user);

    /**
     * 根据用户名获取认证用户信息，携带角色和密码
     *
     * @param username
     * @return
     */
    UserAuthDTO getByUsername(String username);

    /**
     * 重置密码
     *
     * @param password
     * @param user
     * @return
     */
    @Transactional
    boolean resetPassword(String password, SysUser user);

    SysUser detail(String id);
}
