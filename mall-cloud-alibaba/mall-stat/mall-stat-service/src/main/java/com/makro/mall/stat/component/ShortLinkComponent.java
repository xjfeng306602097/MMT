package com.makro.mall.stat.component;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.common.util.hash.ConsistentHash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Set;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/8/1
 */
@Component
@Slf4j
public class ShortLinkComponent implements InitializingBean, RedisConstants {

    @Value("${short.link.host}")
    private String shortLinkHost;

    @Value("${short.link.code.length:6}")
    private Integer shortLinkCodeLength;

    private final Set<String> hashKeys = Set.of("short:link:hash:1",
            "short:link:hash:2",
            "short:link:hash:3",
            "short:link:hash:4",
            "short:link:hash:5");

    private ConsistentHash<String> consistentHash;

    @Resource
    private RedisUtils redisUtils;

    private static final String BASE_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final long TTL = 2592000L;

    @Override
    public void afterPropertiesSet() throws Exception {
        ConsistentHash<String> hash = new ConsistentHash<String>();
        hash.setNodeSet(hashKeys);
        hash.setVirtualCount(hashKeys.size());
        hash.buildHashCircle();
        this.consistentHash = hash;
    }

    public String shortLink(URI uri) {
        String wholeUrl = uri.toString();
        String randomCode = RandomUtil.randomString(BASE_STRING, shortLinkCodeLength);
        String hashKey = null;
        synchronized (this) {
            hashKey = this.consistentHash.findNodeByKey(wholeUrl);
        }
        boolean hHashKey = redisUtils.hHasKey(hashKey, wholeUrl);
        if (hHashKey) {
            String oldCode = (String) redisUtils.hget(hashKey, wholeUrl);
            // 删除原来的短链
            redisUtils.del(SHORT_LINK_CODE_PREFIX + oldCode);
        }
        String shortLink = shortLinkHost + randomCode;
        redisUtils.set(SHORT_LINK_CODE_PREFIX + randomCode, wholeUrl, TTL);
        redisUtils.hset(hashKey, wholeUrl, randomCode);
        return shortLink;
    }

    public void delShortLink(String shortLinkCode) {
        String key = SHORT_LINK_CODE_PREFIX + shortLinkCode;
        String wholeUrl = (String) redisUtils.get(key);
        if (StrUtil.isNotEmpty(wholeUrl)) {
            String hashKey = null;
            synchronized (this) {
                hashKey = this.consistentHash.findNodeByKey(wholeUrl);
            }
            boolean hHashKey = redisUtils.hHasKey(hashKey, wholeUrl);
            if (hHashKey) {
                // 删除原来的短链
                redisUtils.del(SHORT_LINK_CODE_PREFIX + shortLinkCode);
            }
            redisUtils.del(key);
            log.info("short link has been deleted, code : {}, originUrl: {}", shortLinkCode, wholeUrl);
        }
    }

}
