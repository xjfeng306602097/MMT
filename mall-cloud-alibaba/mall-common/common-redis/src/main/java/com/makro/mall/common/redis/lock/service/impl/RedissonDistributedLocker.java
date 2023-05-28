package com.makro.mall.common.redis.lock.service.impl;

import com.makro.mall.common.redis.lock.service.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description DistributedLock
 * @date 2021/12/16
 */
public class RedissonDistributedLocker implements DistributedLock {
    private RedissonClient redissonClient;

    public RedissonDistributedLocker(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RLock lock(String lockKey) {
        RLock rLock = this.getRLock(lockKey);
        rLock.lock();
        return rLock;
    }

    @Override
    public RLock lock(String lockKey, long leaseTime) {
        return this.lock(lockKey, leaseTime, TimeUnit.SECONDS);
    }

    @Override
    public RLock lock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        RLock rLock = this.getRLock(lockKey);
        rLock.lock(leaseTime, timeUnit);
        return rLock;
    }

    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        RLock rLock = this.getRLock(lockKey);
        return rLock.tryLock(waitTime, leaseTime, timeUnit);
    }

    @Override
    public void unLock(String lockKey) {
        RLock rLock = this.getRLock(lockKey);
        rLock.unlock();
    }

    @Override
    public void unLock(RLock rLock) {
        if (null == rLock) {
            throw new NullPointerException("rLock cannot be null.");
        } else {
            rLock.unlock();
        }
    }

    private RLock getRLock(String lockKey) {
        if (null == this.redissonClient) {
            throw new NullPointerException("redisson client is null.");
        } else {
            return this.redissonClient.getLock(lockKey);
        }
    }
}
