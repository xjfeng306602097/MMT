package com.makro.mall.admin.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author: admin
 * @date: 2021/10/19 15:39
 * @description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RefreshPermRolesRules {

    String[] prefix() default {"save", "update", "delete", "remove"};

}
