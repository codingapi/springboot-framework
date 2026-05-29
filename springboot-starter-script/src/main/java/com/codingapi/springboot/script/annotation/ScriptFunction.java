package com.codingapi.springboot.script.annotation;

import java.lang.annotation.*;

/**
 *  脚本函数
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptFunction {

    /**
     * 名称
     */
    String name();

    /**
     * 描述
     */
    String description() default "";

}
