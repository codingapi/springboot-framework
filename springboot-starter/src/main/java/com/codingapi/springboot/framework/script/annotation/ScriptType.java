package com.codingapi.springboot.framework.script.annotation;

import java.lang.annotation.*;

/**
 * 脚本对象
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptType {

    /**
     * 描述
     */
    String description() default "";

}
