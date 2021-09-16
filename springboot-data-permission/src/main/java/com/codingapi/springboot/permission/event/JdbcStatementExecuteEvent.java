package com.codingapi.springboot.permission.event;

import com.codingapi.springboot.framework.event.IEvent;
import com.codingapi.springboot.permission.sql.SQL;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Statement;

/**
 * @author lorne
 * @since 1.0.0
 */
@AllArgsConstructor
public class JdbcStatementExecuteEvent implements IEvent {

    @Getter
    private final Statement statement;
    @Getter
    private final SQL sql;


}
