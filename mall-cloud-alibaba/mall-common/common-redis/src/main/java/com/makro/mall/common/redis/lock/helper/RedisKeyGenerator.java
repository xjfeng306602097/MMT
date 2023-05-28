package com.makro.mall.common.redis.lock.helper;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/12/16
 */
public interface RedisKeyGenerator {

    String generate(ProceedingJoinPoint var1);

}
