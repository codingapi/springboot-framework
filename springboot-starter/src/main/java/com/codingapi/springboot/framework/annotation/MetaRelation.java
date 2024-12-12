package com.codingapi.springboot.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MetaRelation {

    /**
     * 表名称
     */
    String tableName();

    /**
     * 字段名称
     */
    String columnName();
}
