package com.makro.mall.stat.controller;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.web.util.IpUtil;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.stat.mq.producer.SysUserLogProducer;
import com.makro.mall.stat.pojo.entity.SystemUserLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


/**
 * @author jincheng
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sys/log/user")
@Api(tags = "系统用户数据采集")
public class SysUserLogController {

    private final SysUserLogProducer sysUserProducer;


    @PostMapping
    @ApiOperation(value = "添加系统用户日志")
    public BaseResponse add(HttpServletRequest request, @RequestBody SystemUserLog systemUserLog) throws UnknownHostException {
        String ipAddress = StrUtil.isNotBlank(systemUserLog.getUserIp()) ? systemUserLog.getUserIp() : IpUtil.getIpAddr(request);
        systemUserLog.setUserId(JwtUtils.getUserId());
        systemUserLog.setUserName(JwtUtils.getUsername());
        systemUserLog.setUserIp(IpUtil.convertIPv6ToIPv4(ipAddress));
        systemUserLog.setCreateTime(LocalDateTime.now());
        sysUserProducer.save(systemUserLog);
        return BaseResponse.success();
    }


}


