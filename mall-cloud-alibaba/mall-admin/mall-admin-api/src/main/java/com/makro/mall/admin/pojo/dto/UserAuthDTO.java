package com.makro.mall.admin.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * 系统用户认证信息
 *
 * @author xiaojunfeng
 * @date 2021/9/27
 */
@Data
public class UserAuthDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户状态：1-有效；0-禁用
     */
    private Integer status;

    /**
     * 用户角色编码集合 ["ROOT","ADMIN"]
     */
    private List<String> roles;


}
