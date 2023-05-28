package com.makro.mall.admin.aspect;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.admin.aspect.annotation.RefreshPermRolesRules;
import com.makro.mall.admin.service.SysPermissionService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.util.stream.Stream;

/**
 * @author xiaojunfeng
 * @description 刷新用户权限切面，触发相关接口刷新缓存信息
 * @date 2021/10/19
 */
@Aspect
public class RefreshRolePermissionAspect {

    @Resource
    private SysPermissionService sysPermissionService;

    @Around("execution(public * *(..)) && @within(com.makro.mall.admin.aspect.annotation.RefreshPermRolesRules)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        RefreshPermRolesRules permission = (RefreshPermRolesRules) pjp.getTarget().getClass().getAnnotation(RefreshPermRolesRules.class);
        Object obj;
        try {
            String methodName = pjp.getSignature().getName();
            obj = pjp.proceed();
            if (checkMethodName(methodName, permission.prefix())) {
                if (obj instanceof BaseResponse && ((BaseResponse) obj).getCode()
                        .equals(StatusCode.SUCCESS.getCode())) {
                    sysPermissionService.refreshPermRolesRules();
                } else if ((obj instanceof Boolean && (Boolean) obj)) {
                    sysPermissionService.refreshPermRolesRules();
                }
            }
        } catch (Throwable var13) {
            throw var13;
        }
        return obj;
    }

    private Boolean checkMethodName(String methodName, String[] prefix) {
        return Stream.of(prefix).anyMatch(p -> StrUtil.startWithIgnoreCase(methodName, p));
    }

}
