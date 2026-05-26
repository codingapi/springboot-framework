package com.codingapi.springboot.framework.script.annotation;

import java.lang.annotation.*;

/**
 * 脚本对象
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptType {

    /**
     * 名称
     */
    String name() default "";

    /**
     * 描述
     */
    String description() default "";

}
