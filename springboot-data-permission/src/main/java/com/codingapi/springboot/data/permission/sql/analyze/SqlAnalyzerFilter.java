package com.codingapi.springboot.data.permission.sql.analyze;

import com.codingapi.springboot.data.permission.sql.SQL;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface SqlAnalyzerFilter {

    void doFilter(SQL sql);

}
