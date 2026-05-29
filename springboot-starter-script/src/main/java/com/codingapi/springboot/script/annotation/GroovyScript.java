package com.codingapi.springboot.script.annotation;

import java.lang.annotation.*;

/**
 *  脚本字段
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroovyScript {

}
