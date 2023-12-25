package com.codingapi.springboot.persistence.jdbc;

import com.codingapi.springboot.persistence.schema.Schema;
import com.codingapi.springboot.persistence.schema.SchemaExecutor;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcSchemaExecutor implements SchemaExecutor {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public void create(Schema schema) {
        jdbcTemplate.execute(schema.buildSchema().createSchema());
    }


}
