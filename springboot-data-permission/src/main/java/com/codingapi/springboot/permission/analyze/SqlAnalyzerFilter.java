package com.codingapi.springboot.permission.analyze;

import com.codingapi.springboot.permission.sql.SQL;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface SqlAnalyzerFilter {

    void doFilter(SQL sql);

}
