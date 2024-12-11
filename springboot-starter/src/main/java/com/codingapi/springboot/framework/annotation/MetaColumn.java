package com.codingapi.springboot.framework.annotation;

import java.lang.annotation.*;

/**
 *  查询字段
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MetaColumn {

    /**
     * 字段说明
     */
    String desc();

    /**
     * 字段名称
     */
    String name();

    /**
     * 是否主键
     */
    boolean primaryKey() default false;

    /**
     * 字段类型
     */
    ColumnType type() default ColumnType.String;

    /**
     * 格式化
     */
    String format() default "";

    /**
     * 依赖表
     */
    MetaRelation dependent() default @MetaRelation(tableName = "", columnName = "");

}
