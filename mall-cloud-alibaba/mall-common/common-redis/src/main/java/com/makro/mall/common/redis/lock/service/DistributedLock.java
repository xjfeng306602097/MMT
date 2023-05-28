package com.makro.mall.common.redis.lock.service;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/12/16
 */
public interface DistributedLock {

    RLock lock(String var1);

    RLock lock(String var1, long var2);

    RLock lock(String var1, long var2, TimeUnit var4);

    boolean tryLock(String var1, long var2, long var4, TimeUnit var6) throws InterruptedException;

    void unLock(String var1);

    void unLock(RLock var1);

}
