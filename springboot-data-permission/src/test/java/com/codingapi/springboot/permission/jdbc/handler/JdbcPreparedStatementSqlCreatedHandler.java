package com.codingapi.springboot.permission.jdbc.handler;

import com.codingapi.springboot.framework.handler.IHandler;
import com.codingapi.springboot.permission.analyzer.SQL;
import com.codingapi.springboot.permission.event.JdbcPreparedStatementSqlCreatedEvent;
import org.springframework.stereotype.Component;

/**
 * @author lorne
 * @since 1.0.0
 */
@Component
public class JdbcPreparedStatementSqlCreatedHandler implements IHandler<JdbcPreparedStatementSqlCreatedEvent> {

    @Override
    public void handler(JdbcPreparedStatementSqlCreatedEvent event) {
        SQL sql =  event.getSql();
        sql.deleteKey("create_time");
    }
}
