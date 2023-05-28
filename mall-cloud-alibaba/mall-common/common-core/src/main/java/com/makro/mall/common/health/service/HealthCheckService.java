package com.makro.mall.common.health.service;

import com.makro.mall.common.enums.HealthCheckStatusEnum;
import com.makro.mall.common.health.HealthCheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/9
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HealthCheckService {

    private static final Long EXPIRE_TIME = 60L;

    private final String REDIS = "redis";

    private final String ORACLE = "oracle";

    public HealthCheckResult checkRedis(RedisTemplate redisTemplate) {
        log.debug("====== redis check 开始 ======");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HealthCheckResult result = new HealthCheckResult();
        if (redisTemplate == null) {
            log.debug("check, redis does not exists.");
            return result.fillCodeAndMsg(REDIS, HealthCheckStatusEnum.NONE);
        }
        result = new HealthCheckResult();
        if (set(redisTemplate, "util:test:key", "This is for Testing", EXPIRE_TIME)) {
            log.debug("check, set string in redis successfully.");
            result.fillCodeAndMsg(REDIS, HealthCheckStatusEnum.NORMAL);
        } else {
            log.warn("check, set string in redis failed.");
            result.fillCodeAndMsg(REDIS, HealthCheckStatusEnum.ABNORMAL);
        }
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.debug("检测redis任务执行时间: [{}] ms", totalTimeMillis);
        result.setMills(totalTimeMillis);
        log.debug("====== redis check 结束 ======");
        return result;
    }

    public HealthCheckResult checkOracle(DataSource dataSource) {
        log.debug("====== oracle check 开始 ======");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HealthCheckResult result = new HealthCheckResult();
        if (dataSource == null) {
            log.debug("check, oracle does not exists.");
            return result.fillCodeAndMsg(ORACLE, HealthCheckStatusEnum.NONE);
        }
        result = new HealthCheckResult();
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            log.debug("check, Database connection established");
            PreparedStatement pstmt = connection.prepareStatement("SELECT 1 FROM DUAL");
            ResultSet rset = pstmt.executeQuery();

            while (rset.next()) {
                log.debug("check, Database connection successful，print：[{}]", rset.getString(1));
            }

            result.fillCodeAndMsg(ORACLE, HealthCheckStatusEnum.NORMAL);
        } catch (Exception var17) {
            log.warn("check, Cannot connect to database server", var17);
            result.fillCodeAndMsg(ORACLE, HealthCheckStatusEnum.ABNORMAL);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    log.debug("check, Database connection terminated");
                } catch (Exception var16) {
                    log.warn("check, Database close exception happened", var16);
                }
            }
        }
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.debug("检测oracle任务执行时间: [{}] ms", totalTimeMillis);
        result.setMills(totalTimeMillis);
        log.debug("====== oracle check 结束 ======");
        return result;
    }

    public boolean set(RedisTemplate redisTemplate, String key, Object value, long time) {
        try {
            if (time > 0L) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

}
