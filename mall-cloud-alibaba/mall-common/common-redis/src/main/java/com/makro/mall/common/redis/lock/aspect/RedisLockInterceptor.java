package com.makro.mall.common.redis.lock.aspect;

import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.redis.lock.annotation.RedisLock;
import com.makro.mall.common.redis.lock.helper.RedisKeyGenerator;
import com.makro.mall.common.redis.lock.service.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/12/16
 */
@Aspect
public class RedisLockInterceptor {

    @Autowired
    private DistributedLock distributedLock;
    @Autowired
    private RedisKeyGenerator redisKeyGenerator;

    public RedisLockInterceptor() {
    }

    @Around("execution(public * *(..)) && @annotation(com.makro.mall.common.redis.lock.annotation.RedisLock)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RedisLock lock = (RedisLock) method.getAnnotation(RedisLock.class);
        String lockKey = this.redisKeyGenerator.generate(pjp);

        Object var7;
        try {
            boolean success = this.distributedLock.tryLock(lockKey, (long) lock.waitTime(), (long) lock.expire(), lock.timeUnit());
            if (!success && lock.throwError()) {
                throw new BusinessException(lock.message());
            }
            var7 = pjp.proceed();
        } catch (InterruptedException var12) {
            throw new BusinessException("异常:获取Redis锁中断");
        } catch (Throwable var13) {
            throw var13;
        } finally {
            this.distributedLock.unLock(lockKey);
        }

        return var7;
    }

}
