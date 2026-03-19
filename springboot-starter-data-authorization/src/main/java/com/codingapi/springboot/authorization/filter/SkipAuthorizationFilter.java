package com.codingapi.springboot.authorization.filter;


/**
 *  跳过数据权限拦截处理机制
 */
public interface SkipAuthorizationFilter {

    String filter(String sql);

}