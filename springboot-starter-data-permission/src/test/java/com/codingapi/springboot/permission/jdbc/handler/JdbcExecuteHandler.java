package com.codingapi.springboot.permission.jdbc.handler;

import com.codingapi.springboot.framework.handler.IHandler;
import com.codingapi.springboot.permission.event.JdbcPreparedStatementExecuteEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lorne
 * @since 1.0.0
 */
@Component
@Slf4j
public class JdbcExecuteHandler implements IHandler<JdbcPreparedStatementExecuteEvent> {

    @SneakyThrows
    @Override
    public void handler(JdbcPreparedStatementExecuteEvent event) {
        event.put("user_id",1);
    }
}
