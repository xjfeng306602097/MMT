package com.makro.mall.common.web.aspect;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.web.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author jincheng
 */
@Aspect
@Component
@Slf4j
public class BizLogInterceptor {
    @Pointcut("execution(public * com.makro.mall.*.controller.*.*(..))")
    public void bizLog() {
    }

    @Before("bizLog()")
    public void doBefore(JoinPoint joinPoint) {
        String url = null;
        String authorization = null;
        try {
            //收到请求,记录请求内容
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            HttpServletRequest httpServletRequest = requestAttributes.getRequest();

            //排除类型
            List<Object> args = Arrays.stream(joinPoint.getArgs()).filter(x -> !(x instanceof MultipartFile)).collect(Collectors.toList());

            //白名单
            authorization = httpServletRequest.getHeader("Authorization");
            url = httpServletRequest.getMethod() + httpServletRequest.getRequestURL().toString();


            log.info("REQUEST : " +
                            "\n**********************************************************************" +
                            "\n* URL:{}" +
                            "\n* ARGS: {}" +
                            "\n* USERNAME: {}" +
                            "\n**********************************************************************",
                    url,
                    args,
                    StrUtil.isBlank(authorization) || StrUtil.equals("Basic bWFsbC1hZG1pbi13ZWI6MTIzNDU2", authorization) ? "" : JwtUtils.getUsername(authorization)
            );
        } catch (Exception e) {
            log.error("BizLogInterceptor error: URL:{} authorization:{} e:{}", url, authorization, e);
        }

    }

    //@AfterReturning(returning = "res", pointcut = "bizLog()")
    //public void doAfterReturning(BaseResponse res) {
    //    try {
    //        //处理完请求，返回内容
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        objectMapper.registerModule(new JavaTimeModule());
    //        if (res.getData() instanceof byte[]) {
    //            return;
    //        }
    //        log.info("RESPONSE : " + objectMapper.writeValueAsString(res));
    //    } catch (Exception e) {
    //        log.error("BizLogInterceptor error:", e);
    //    }
    //}


}
