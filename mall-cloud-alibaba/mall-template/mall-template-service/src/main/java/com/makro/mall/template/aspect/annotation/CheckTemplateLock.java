package com.makro.mall.template.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author: admin
 * @date: 2021/10/19 15:39
 * @description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CheckTemplateLock {

    String key() default "";

    String msg() default "Current template {%s} has been locked by {%s}";

}
