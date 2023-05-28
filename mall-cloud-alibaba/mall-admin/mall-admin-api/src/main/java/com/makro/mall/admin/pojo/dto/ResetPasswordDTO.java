package com.makro.mall.admin.pojo.dto;

import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/10
 */
@Data
public class ResetPasswordDTO {

    private String username;

    private String password;

    private String token;

}
