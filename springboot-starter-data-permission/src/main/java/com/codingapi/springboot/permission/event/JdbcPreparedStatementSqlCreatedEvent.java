package com.codingapi.springboot.permission.event;

import com.codingapi.springboot.framework.event.IEvent;
import com.codingapi.springboot.permission.analyzer.SQL;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lorne
 * @since 1.0.0
 */
@AllArgsConstructor
public class JdbcPreparedStatementSqlCreatedEvent implements IEvent {

    @Getter
    private final SQL sql;

}
