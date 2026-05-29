package com.codingapi.springboot.script.annotation;

import java.lang.annotation.*;

/**
 * 脚本函数参数
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptParameter {

    /**
     * 名称
     */
    String name() default "";

    /**
     * 描述
     */
    String description() default "";

}
