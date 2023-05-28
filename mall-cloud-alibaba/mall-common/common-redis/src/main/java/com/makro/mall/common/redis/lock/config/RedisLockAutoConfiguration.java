package com.makro.mall.common.redis.lock.config;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.redis.lock.aspect.RedisLockInterceptor;
import com.makro.mall.common.redis.lock.helper.RedisKeyGenerator;
import com.makro.mall.common.redis.lock.helper.impl.DefaultRedisKeyGenerator;
import com.makro.mall.common.redis.lock.service.DistributedLock;
import com.makro.mall.common.redis.lock.service.impl.RedissonDistributedLocker;
import com.makro.mall.common.redis.redisson.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/12/16
 */
@ConditionalOnClass({Config.class})
@EnableConfigurationProperties({RedissonProperties.class})
public class RedisLockAutoConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    public RedisLockAutoConfiguration() {
    }

    @Bean
    @ConditionalOnProperty(
            name = {"redission.master-name"}
    )
    public RedissonClient redissonSentinel() {
        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = (SentinelServersConfig) ((SentinelServersConfig) ((SentinelServersConfig) config.useSentinelServers().setDatabase(this.redissonProperties.getDatabase()).addSentinelAddress(this.redissonProperties.getSentinelAddresses()).setMasterName(this.redissonProperties.getMasterName()).setTimeout(this.redissonProperties.getTimeout())).setMasterConnectionPoolSize(this.redissonProperties.getMasterConnectionPoolSize())).setSlaveConnectionPoolSize(this.redissonProperties.getSlaveConnectionPoolSize());
        if (StrUtil.isNotEmpty(this.redissonProperties.getPassword())) {
            sentinelServersConfig.setPassword(this.redissonProperties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnProperty(
            name = {"redisson.address"}
    )
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = ((SingleServerConfig) config.useSingleServer().setAddress("redis://" + this.redissonProperties.getAddress().trim()).setDatabase(this.redissonProperties.getDatabase()).setTimeout(this.redissonProperties.getTimeout())).setConnectionPoolSize(this.redissonProperties.getConnectionPoolSize()).setConnectionMinimumIdleSize(this.redissonProperties.getConnectionMinimumIdleSize());
        if (StrUtil.isNotEmpty(this.redissonProperties.getPassword())) {
            singleServerConfig.setPassword(this.redissonProperties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    public DistributedLock distributedLock(RedissonClient redissonClient) {
        return new RedissonDistributedLocker(redissonClient);
    }

    @Bean
    @ConditionalOnExpression("'${redisson.lock.keygenerator:default}'.equals('default')")
    public RedisKeyGenerator redisKeyGenerator() {
        return new DefaultRedisKeyGenerator();
    }

    @Bean
    @ConditionalOnBean({DistributedLock.class, RedisKeyGenerator.class})
    @ConditionalOnExpression("'${redisson.lock.aspect:default}'.equals('default')")
    protected RedisLockInterceptor cacheAspect() {
        return new RedisLockInterceptor();
    }

}
