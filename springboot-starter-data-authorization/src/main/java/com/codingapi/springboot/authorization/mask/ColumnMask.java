package com.codingapi.springboot.authorization.mask;

/**
 * 列数据脱敏
 */
public interface ColumnMask {

    boolean support(Object value);

    Object mask(Object value);

}
