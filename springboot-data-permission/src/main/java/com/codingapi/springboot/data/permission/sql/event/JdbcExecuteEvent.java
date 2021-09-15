package com.codingapi.springboot.data.permission.sql.event;

import com.codingapi.springboot.data.permission.sql.JdbcSql;
import com.codingapi.springboot.framework.event.IEvent;
import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author lorne
 * @since 1.0.0
 */
@AllArgsConstructor
public class JdbcExecuteEvent implements IEvent {

    private final PreparedStatement preparedStatement;
    private final JdbcSql jdbcSql;

    public void put(String key,Object value) throws SQLException {
        jdbcSql.put(key,value,(index)->{
            preparedStatement.setObject(index,value);
        });
    }


}
