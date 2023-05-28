package com.makro.mall.common.redis.lock.annotation;

import com.makro.mall.common.redis.lock.config.RedisLockAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: admin
 * @date: 2021/12/16 16:37
 * @description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RedisLockAutoConfiguration.class})
public @interface EnableRedisLock {
}