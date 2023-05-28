package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SysUserRole {

    @TableField(value = "USER_ID")
    private String userId;

    @TableField(value = "ROLE_ID")
    private Long roleId;

}
