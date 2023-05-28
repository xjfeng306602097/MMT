package com.makro.mall.admin.pojo.vo;

import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/2
 */
@Data
public class MmDataAuthVO {

    private String access_token;

    private String token_type;

    private String refresh_token;

    private Long expires_in;

}
