package com.codingapi.springboot.data.permission.sql.analyze;

import java.util.List;

public class SqlAnalyzer {

    private final String sql;

    public SqlAnalyzer(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        String sql = this.sql;
        List<SqlAnalyzerFilter> filters =  AnalyzerFilterContext.getInstance().getFilters();
        for(SqlAnalyzerFilter filter:filters){
            sql = filter.doFilter(sql);
        }
        return sql;
    }
}
