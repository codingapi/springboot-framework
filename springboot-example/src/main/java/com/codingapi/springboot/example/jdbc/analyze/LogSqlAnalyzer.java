package com.codingapi.springboot.example.jdbc.analyze;

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
    public String doFilter(String sql) {
        //todo
        log.info("sql:{}",sql);
        return sql;
    }
}
