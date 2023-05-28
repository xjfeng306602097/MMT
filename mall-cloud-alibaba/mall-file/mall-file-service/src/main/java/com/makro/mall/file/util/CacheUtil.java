package com.makro.mall.file.util;

import com.alibaba.nacos.common.utils.MD5Utils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.makro.mall.common.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 */
@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final RedisUtils redisUtils;

    private static final Cache<String, Object> CACHE_MAP = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .concurrencyLevel(1)
            .recordStats()
            .build();

    public void put(String key, Object val) {
        CACHE_MAP.put(key, val);
        redisUtils.set(MD5Utils.encodeHexString(key.getBytes()), val, 600000);

    }

    public Object get(String key) throws ExecutionException {
        Object val = CACHE_MAP.getIfPresent(key);
        return val == null ? redisUtils.get(MD5Utils.encodeHexString(key.getBytes())) : val;
    }
}
