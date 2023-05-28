package com.makro.mall.message;

import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * 自定义的测试注解
 *
 * @author xiaojunfeng
 * @since 2021/4/12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DisabledOnOs(OS.LINUX)
@SpringBootTest
@ActiveProfiles("local")
public @interface CustomSpringbootTest {

}
