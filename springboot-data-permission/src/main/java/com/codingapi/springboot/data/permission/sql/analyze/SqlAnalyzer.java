package com.codingapi.springboot.data.permission.sql.analyze;

import com.codingapi.springboot.data.permission.sql.SQL;

import java.util.List;

public class SqlAnalyzer {

    private final SQL sql;

    public SqlAnalyzer(String sql) {
        this.sql = new SQL(sql);
    }


    public SQL getSql() {
        List<SqlAnalyzerFilter> filters =  AnalyzerFilterContext.getInstance().getFilters();
        for(SqlAnalyzerFilter filter:filters){
            filter.doFilter(sql);
        }
        return sql;
    }
}
