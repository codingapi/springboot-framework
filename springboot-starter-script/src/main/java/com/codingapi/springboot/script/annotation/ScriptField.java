package com.codingapi.springboot.script.annotation;

import java.lang.annotation.*;

/**
 * 脚本字段
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptField {

    /**
     * 名称
     */
    String name() default "";

    /**
     * 描述
     */
    String description() default "";

}
