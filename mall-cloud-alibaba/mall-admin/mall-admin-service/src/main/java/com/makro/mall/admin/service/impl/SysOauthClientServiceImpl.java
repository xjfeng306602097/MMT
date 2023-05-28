package com.makro.mall.admin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.SysOauthClientMapper;
import com.makro.mall.admin.pojo.entity.SysOauthClient;
import com.makro.mall.admin.service.SysOauthClientService;
import org.springframework.stereotype.Service;

/**
 * @author xiaojunfeng
 * @description 认证客户端服务类
 * @date 2021/10/13
 */
@Service
public class SysOauthClientServiceImpl extends ServiceImpl<SysOauthClientMapper, SysOauthClient> implements SysOauthClientService {
}
