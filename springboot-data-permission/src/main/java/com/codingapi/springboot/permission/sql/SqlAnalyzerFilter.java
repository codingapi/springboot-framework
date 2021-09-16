package com.codingapi.springboot.permission.sql;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface SqlAnalyzerFilter {

    void doFilter(SQL sql);

    boolean match(SQL sql);

    String delete(String key,String sql);
}
