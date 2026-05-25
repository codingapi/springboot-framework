package com.codingapi.springboot.framework.script.annotation;

import java.lang.annotation.*;

/**
 *  脚本函数对象定义
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

    /**
     * 参数对象
     */
    ScriptType[] requests() default {};

    /**
     * 返回数据类型
     */
    Class<?> returnType();

}
