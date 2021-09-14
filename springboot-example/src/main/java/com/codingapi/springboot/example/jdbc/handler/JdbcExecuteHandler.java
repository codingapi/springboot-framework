package com.codingapi.springboot.example.jdbc.handler;

import com.codingapi.springboot.data.permission.sql.JdbcSql;
import com.codingapi.springboot.data.permission.sql.event.JdbcExecuteEvent;
import com.codingapi.springboot.framework.handler.BaseHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

/**
 * @author lorne
 * @since 1.0.0
 */
@Component
@Slf4j
public class JdbcExecuteHandler extends BaseHandler<JdbcExecuteEvent> {

    @SneakyThrows
    @Override
    public void handler0(JdbcExecuteEvent event) {
        //todo
        JdbcSql jdbcSql =  event.getJdbcSql();
        PreparedStatement preparedStatement = event.getPreparedStatement();
        preparedStatement.setObject(2,1);
        log.info("set parameterIndex 2 set value 1");
    }
}
