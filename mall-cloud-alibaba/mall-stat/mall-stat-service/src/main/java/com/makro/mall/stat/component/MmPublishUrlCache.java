package com.makro.mall.stat.component;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MessageStatusCode;
import com.makro.mall.common.util.AesBase62Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Component
@Slf4j
@RequiredArgsConstructor
public class MmPublishUrlCache {


    /**
     * lock 记录每一个mmCode当前的查询情况
     */
    static Map<String, ReentrantLock> mapLock = new ConcurrentHashMap<>();
    /**
     * 限制查询MM数量
     */
    static Semaphore semaphore = new Semaphore(5);

    private final LoadingCache<String, MmActivity> CACHE = CacheBuilder.newBuilder()
            .maximumSize(5)
            .concurrencyLevel(8)
            .expireAfterWrite(10, TimeUnit.DAYS)
            .build(new CacheLoader<>() {
                @Override
                public MmActivity load(@NotNull String mmCode) {
                    return mmActivityFeignClient.getPublishUrlByCode(mmCode).getData();
                }
            });


    private final MmActivityFeignClient mmActivityFeignClient;
    private final CustomerFeignClient customerFeignClient;
    private final RedisTemplate<String, String> redisTemplate;


    public Boolean delMailUrl(String lineId, String mmCode, String pageNo) {
        Assert.notNull(ObjectUtil.isNotNull(mmCode), MessageStatusCode.MMCODE_IS_NOT_NULL);
        String key = getKey(lineId, mmCode, pageNo);
        Boolean delete = redisTemplate.delete(key);
        log.info("delMailUrl删除key:{}", key);
        return delete;
    }

    public String getMailUrl(String lineId, String mmCode, String pageNo) {
        Assert.notNull(mmCode, MessageStatusCode.MMCODE_IS_NOT_NULL);
        String key = getKey(lineId, mmCode, pageNo);
        String url = redisTemplate.opsForValue().get(key);
        // 命中缓存则退出
        if (ObjectUtil.isNotNull(url)) {
            log.info("getMailUrl缓存命中key:{}", key);
            return url;
        }

        // 缓存miss之后只允许一个线程查询数据库
        ReentrantLock reentrantLock = new ReentrantLock();
        if (ObjectUtil.isNotNull(mapLock.putIfAbsent(mmCode, reentrantLock))) {
            reentrantLock = mapLock.get(mmCode);
        }
        reentrantLock.lock();
        try {

            // 1. 再次查询缓存避免大量重复数据库查询
            url = redisTemplate.opsForValue().get(key);
            if (ObjectUtil.isNotNull(url)) {
                log.info("getPublishUrl缓存命中key:{}", key);
                return url;
            }

            semaphore.acquire();

            // 2. 如果缓存miss，则查询数据库
            MmActivity mmActivity = CACHE.get(mmCode);
            if (ObjectUtil.isNull(mmActivity)) {
                log.error("getPublishUrl报错,数据库找不到mmCode为:{} 的MM发布地址", mmCode);
                return url;
            }
            log.info("getPublishUrl缓存miss key:{}", key);
            // 3. 设置缓存 就算查到空也缓存
            //查询用户lineId对应userId 进行加密
            Long userId = customerFeignClient.getByLineId(lineId).getData();
            String c = ObjectUtil.isNull(userId) ? "" : AesBase62Util.encode(userId);

            url = mmActivity.getPublishUrl()
                    + "?q=line"
                    + "&c=" + c
                    + "&p=" + pageNo
                    + "&s=" + mmActivity.getStoreCode();
            redisTemplate.opsForValue().setIfAbsent(key, url, 30, TimeUnit.DAYS);

            semaphore.release();
            log.info("getMailUrl return lineId:{} userId:{} mmCode:{} return:{}", lineId, userId, mmCode, url);
            return url;
        } catch (Exception e) {
            log.error("getPublishUrl缓存报错:{} e:{}", key, e);
        } finally {
            if (!reentrantLock.hasQueuedThreads()) {
                mapLock.remove(mmCode);
            }
            reentrantLock.unlock();
        }
        return null;
    }

    private String getKey(String lineId, String mmCode, String pageNo) {
        StringBuilder sb = new StringBuilder("stat:publishUrl");
        if (StrUtil.isNotBlank(mmCode)) {
            sb.append(":mm:").append(mmCode);
        }
        if (StrUtil.isNotBlank(lineId)) {
            sb.append(":lineUserId:").append(lineId);
        }
        if (StrUtil.isNotBlank(pageNo)) {
            sb.append(":pageNo:").append(pageNo);
        }
        return sb.toString();
    }

}
