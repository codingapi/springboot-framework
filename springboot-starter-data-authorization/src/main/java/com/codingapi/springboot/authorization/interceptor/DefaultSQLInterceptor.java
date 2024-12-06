package com.codingapi.springboot.authorization.interceptor;



import com.codingapi.springboot.authorization.analyzer.SelectSQLAnalyzer;
import com.codingapi.springboot.authorization.handler.RowHandler;
import com.codingapi.springboot.authorization.handler.RowHandlerContext;
import com.codingapi.springboot.authorization.utils.SQLUtils;

import java.sql.SQLException;

public class DefaultSQLInterceptor implements SQLInterceptor {

    @Override
    public boolean beforeHandler(String sql) {
        return SQLUtils.isQuerySql(sql);
    }


    @Override
    public void afterHandler(String sql, String newSql, SQLException exception) {

    }

    @Override
    public String postHandler(String sql) throws SQLException {
        RowHandler rowHandler = RowHandlerContext.getInstance().getRowHandler();
        SelectSQLAnalyzer sqlAnalyzerInterceptor = new SelectSQLAnalyzer(sql, rowHandler);
        return sqlAnalyzerInterceptor.getNewSQL();
    }
}
