package com.makro.mall.admin.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {

    private String id;

    private String username;

    private String nickname;

    private String avatar;

    private List<String> roles;

    private List<String> perms;

    private String lang;

}
