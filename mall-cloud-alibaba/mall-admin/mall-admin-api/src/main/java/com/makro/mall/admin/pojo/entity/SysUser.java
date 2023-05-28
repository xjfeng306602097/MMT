package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
public class SysUser extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String nickname;

    private String password;

    private Long deptId;

    private Integer gender;

    private String avatar;

    private String phone;

    private String email;

    private Integer status;

    private String lang;

    private Integer loginNum;

    private String lastLoginIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private List<Long> roleIds = new ArrayList<>();

    @TableField(exist = false)
    private String roleNames;

    @TableField(exist = false)
    private String lastLoginTimeStart;

    @TableField(exist = false)
    private String lastLoginTimeEnd;

    @TableField(exist = false)
    private List<String> roles = new ArrayList<>();

    public SysUser init() {
        this.status = 1;
        this.deleted = 0;
        return this;
    }

}
