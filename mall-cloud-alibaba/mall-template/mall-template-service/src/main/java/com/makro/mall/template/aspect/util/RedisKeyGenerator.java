package com.makro.mall.template.aspect.util;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.TemplateStatusCode;
import com.makro.mall.template.aspect.annotation.CheckTemplateLock;
import com.makro.mall.template.constants.RedisCacheConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author xiaojunfeng
 * @description redis key生成器
 * @date 2021/11/1
 */
@Component
public class RedisKeyGenerator {

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

    public String generate(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Object target = joinPoint.getTarget();
        Object[] arguments = joinPoint.getArgs();
        CheckTemplateLock checkTemplateLock = (CheckTemplateLock) AnnotationUtils.findAnnotation(targetMethod, CheckTemplateLock.class);
        StringBuilder key = new StringBuilder();
        String spel = checkTemplateLock.key();
        Assert.isTrue(StrUtil.isNotEmpty(spel), TemplateStatusCode.CHECKTEMPLATELOCK_NEEDS_TO_SPECIFY_KEY);//

        return key.append(RedisCacheConstant.TEMPLATE_LOCK_PREFIX).append(parse(target, spel, targetMethod, arguments)).toString();
    }

}
