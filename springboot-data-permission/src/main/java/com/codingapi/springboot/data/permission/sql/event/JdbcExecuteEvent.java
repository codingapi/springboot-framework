package com.codingapi.springboot.data.permission.sql.event;

import com.codingapi.springboot.data.permission.sql.JdbcSql;
import com.codingapi.springboot.framework.event.IEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.PreparedStatement;

/**
 * @author lorne
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public class JdbcExecuteEvent implements IEvent {

    private final PreparedStatement preparedStatement;
    private final JdbcSql jdbcSql;

}
