package com.codingapi.springboot.script.annotation;

import java.lang.annotation.*;

/**
 *  脚本字段,用于获取对象下的脚本key数据
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroovyScript {

}
