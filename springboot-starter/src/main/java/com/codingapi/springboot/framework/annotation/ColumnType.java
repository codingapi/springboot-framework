package com.codingapi.springboot.framework.annotation;

/**
 *  数据库字段类型
 */
public enum ColumnType {

    /**
     * 整数
     */
    Number,

    /**
     * 浮点数
     */
    Float,

    /**
     * 字符串
     */
    String,

    /**
     * 日期
     */
    Date,

    /**
     * 文件
     */
    File,

    /**
     * 布尔
     */
    Boolean,

    /**
     * 字节
     */
    Bytes,

    /**
     * JSON
     */
    JSON,

    /**
     * 任意
     */
    Any
}
