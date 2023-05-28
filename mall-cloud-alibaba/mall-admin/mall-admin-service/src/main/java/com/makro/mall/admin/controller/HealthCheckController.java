package com.makro.mall.admin.controller;

import com.google.common.collect.Lists;
import com.makro.mall.common.health.HealthCheckResult;
import com.makro.mall.common.health.service.HealthCheckService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 健康检查控制器
 * @date 2022/3/9
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(hidden = true)
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    private final DataSource dataSource;

    private final RedisTemplate redisTemplate;

    @GetMapping("/health-check")
    public BaseResponse<List<HealthCheckResult>> getProbeInfo() {
        log.debug("====== 健康检测 开始 ======");
        HealthCheckResult oracle = healthCheckService.checkOracle(dataSource);
        HealthCheckResult redis = healthCheckService.checkRedis(redisTemplate);
        log.debug("====== 健康检测 结束 ======");
        return BaseResponse.success(Lists.newArrayList(oracle, redis));
    }

    @GetMapping("/now")
    public BaseResponse<String> now() {
        return BaseResponse.success(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    }

}
