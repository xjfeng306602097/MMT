package com.makro.mall.common.redis.lock.helper.impl;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.redis.lock.annotation.RedisLock;
import com.makro.mall.common.redis.lock.helper.RedisKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/12/16
 */
public class DefaultRedisKeyGenerator implements RedisKeyGenerator {
    public DefaultRedisKeyGenerator() {
    }

    @Override
    public String generate(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Object target = joinPoint.getTarget();
        Object[] arguments = joinPoint.getArgs();
        RedisLock redisLock = (RedisLock) AnnotationUtils.findAnnotation(targetMethod, RedisLock.class);
        StringBuilder key = new StringBuilder();
        String spel = null;
        if (redisLock != null) {
            spel = redisLock.key();
        }

        return StrUtil.isNotEmpty(spel) ? key.append("LOCK:").append(redisLock.prefix()).append(redisLock.delimiter()).append(parse(target, spel, targetMethod, arguments)).toString() : key.append("LOCK:").append(redisLock.prefix()).toString();
    }

    public static String parse(Object rootObject, String spel, Method method, Object[] args) {
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, args, u);

        for (int i = 0; i < paraNameArr.length; ++i) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        return (String) parser.parseExpression(spel).getValue(context, String.class);
    }
}
