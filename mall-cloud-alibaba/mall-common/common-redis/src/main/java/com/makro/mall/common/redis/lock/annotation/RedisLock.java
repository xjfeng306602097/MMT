package com.makro.mall.common.redis.lock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: admin
 * @date: 2021/12/16 16:37
 * @description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisLock {

    String prefix();

    String key() default "";

    String delimiter() default ":";

    int expire() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    int waitTime() default 0;

    String message() default "The resource is already occupied!";

    boolean throwError() default true;

}
