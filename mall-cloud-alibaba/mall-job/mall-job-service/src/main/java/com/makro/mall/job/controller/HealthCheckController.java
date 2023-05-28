package com.makro.mall.job.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.makro.mall.common.health.HealthCheckResult;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author 卢嘉俊
 * @description 健康检查控制器
 * @date 2022/6/17
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(hidden = true)
public class HealthCheckController {

    private static final LocalDateTime LDT = LocalDateTime.now();

    @Value("${job.mode}")
    private String mode;

    @GetMapping("/health-check")
    public BaseResponse<List<HealthCheckResult>> getProbeInfo() {
        log.debug("====== 健康检测 开始 ======");
        log.debug("====== 健康检测 结束 ======");
        return BaseResponse.success(Lists.newArrayList());
    }

    @GetMapping("/getValidNetworkInterfaces")
    public Object getValidNetworkInterfaces() throws SocketException {
        return JSON.toJSON(NetworkInterface.getNetworkInterfaces());
    }
    @GetMapping("/getTime")
    public LocalDateTime getTime() {
        return LDT;
    }

    @GetMapping("/mode")
    public String mode() {
        return mode;
    }

    @GetMapping("/getenv")
    public Map<String, String> getenv() {
        return System.getenv();
    }

}
