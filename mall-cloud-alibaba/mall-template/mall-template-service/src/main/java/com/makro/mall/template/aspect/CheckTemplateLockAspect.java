package com.makro.mall.template.aspect;


import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.template.aspect.annotation.CheckTemplateLock;
import com.makro.mall.template.aspect.util.RedisKeyGenerator;
import com.makro.mall.template.constants.RedisCacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * @author xiaojunfeng
 * @description 刷新用户权限切面，触发相关接口刷新缓存信息
 * @date 2021/10/19
 */
@Aspect
@Slf4j
public class CheckTemplateLockAspect {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisKeyGenerator redisKeyGenerator;

    @Around("execution(public * *(..)) && @annotation(com.makro.mall.template.aspect.annotation.CheckTemplateLock)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CheckTemplateLock checkTemplateLock = (CheckTemplateLock) method.getAnnotation(CheckTemplateLock.class);
        String key = this.redisKeyGenerator.generate(pjp);
        String userId = (String) redisUtils.get(key);
        // 获取当前用户ID
        String currentUserId = JwtUtils.getUserId();
        // 当前模板没有被锁，直接加锁
        if (userId == null && StrUtil.isNotEmpty(currentUserId)) {
            log.info("锁定模板,key{},userId:{}", key, currentUserId);
            redisUtils.set(key, currentUserId, RedisCacheConstant.EXPIRE_TIME);
        }
        String code = Stream.of(key.split(":")).reduce((first, second) -> second).get();
        Assert.isTrue(StrUtil.isEmpty(userId) || currentUserId.equals(userId), String.format(checkTemplateLock.msg(), code, userId));
        // 确定是当前用户,给锁续命
        redisUtils.expire(key, RedisCacheConstant.EXPIRE_TIME);
        Object obj;
        try {
            obj = pjp.proceed();
        } catch (Throwable var13) {
            throw var13;
        }
        return obj;
    }


}
