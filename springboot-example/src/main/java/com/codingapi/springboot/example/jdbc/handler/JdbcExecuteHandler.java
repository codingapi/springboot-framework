package com.codingapi.springboot.example.jdbc.handler;

import com.codingapi.springboot.data.permission.sql.event.JdbcExecuteEvent;
import com.codingapi.springboot.framework.handler.BaseHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        event.put("user_id",1);
    }
}
