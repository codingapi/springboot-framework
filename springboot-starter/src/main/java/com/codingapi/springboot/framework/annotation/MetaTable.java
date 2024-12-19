package com.codingapi.springboot.framework.annotation;

import java.lang.annotation.*;

/**
 *  查询表
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MetaTable {

    /**
     * 表说明
     */
    String desc();

    /**
     * 表名称
     */
    String name();

}
