package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.dto.UserAuthDTO;
import com.makro.mall.admin.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<SysUser> list(Page<SysUser> page, SysUser user);

    UserAuthDTO getByUsername(String username);
}
