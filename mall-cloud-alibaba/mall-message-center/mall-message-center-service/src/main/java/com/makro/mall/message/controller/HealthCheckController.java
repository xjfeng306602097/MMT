package com.makro.mall.message.controller;

import com.google.common.collect.Lists;
import com.makro.mall.common.enums.HealthCheckStatusEnum;
import com.makro.mall.common.health.HealthCheckResult;
import com.makro.mall.common.health.service.HealthCheckService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.repository.MailMessageRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final MailMessageRepository mailMessageRepository;

    private final RedisTemplate redisTemplate;

    @GetMapping("/health-check")
    public BaseResponse<List<HealthCheckResult>> getProbeInfo() {
        log.debug("====== 健康检测 开始 ======");
        HealthCheckResult redis = healthCheckService.checkRedis(redisTemplate);

        log.debug("====== mongodb check 开始 ======");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HealthCheckResult result = new HealthCheckResult();
        try {
            mailMessageRepository.findFirstById("a");
            result.fillCodeAndMsg("mongodb", HealthCheckStatusEnum.NORMAL);
        } catch (Exception e) {
            log.warn("check, Cannot connect to mongodb", e);
            result.fillCodeAndMsg("mongodb", HealthCheckStatusEnum.ABNORMAL);
        }
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.debug("检测mongodb任务执行时间: [{}] ms", totalTimeMillis);
        result.setMills(totalTimeMillis);
        log.debug("====== mongodb check 结束 ======");
        log.debug("====== 健康检测 结束 ======");
        return BaseResponse.success(Lists.newArrayList(redis, result));
    }


}
