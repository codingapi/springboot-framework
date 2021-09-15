package com.codingapi.springboot.data.permission.jdbc.analyze;

import com.codingapi.springboot.data.permission.sql.SQL;
import com.codingapi.springboot.data.permission.sql.analyze.SqlAnalyzerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
@Component
public class LogSqlAnalyzer implements SqlAnalyzerFilter {

    @Override
    public void doFilter(SQL sql) {
        sql.deleteKey("create_time");
        log.info("sql:{}",sql);

    }
}
