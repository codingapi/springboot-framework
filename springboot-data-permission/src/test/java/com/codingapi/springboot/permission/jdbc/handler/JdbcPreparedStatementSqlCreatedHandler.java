package com.codingapi.springboot.permission.jdbc.handler;

import com.codingapi.springboot.framework.handler.BaseHandler;
import com.codingapi.springboot.permission.event.JdbcPreparedStatementSqlCreatedEvent;
import com.codingapi.springboot.permission.analyzer.SQL;
import org.springframework.stereotype.Component;

/**
 * @author lorne
 * @since 1.0.0
 */
@Component
public class JdbcPreparedStatementSqlCreatedHandler extends BaseHandler<JdbcPreparedStatementSqlCreatedEvent> {

    @Override
    public void handler0(JdbcPreparedStatementSqlCreatedEvent event) {
        SQL sql =  event.getSql();
        sql.deleteKey("create_time");
    }
}
