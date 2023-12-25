package com.codingapi.springboot.persistence.jdbc.impl;

import com.codingapi.springboot.persistence.DomainPersistence;
import com.codingapi.springboot.persistence.scanner.SchemaContext;
import com.codingapi.springboot.persistence.schema.SaveSchema;
import com.codingapi.springboot.persistence.schema.Schema;
import com.codingapi.springboot.persistence.schema.SearchSchema;
import com.codingapi.springboot.persistence.schema.UpdateSchema;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@AllArgsConstructor
public class JdbcDomainPersistence implements DomainPersistence {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(Object domain) {
        Schema schema = SchemaContext.getInstance().getSchema(domain.getClass());
        if (schema != null) {
            SaveSchema saveSchema = schema.saveSchema();
            if (schema.getSchemaProperty().hasIdValue(domain)) {
                jdbcTemplate.update(saveSchema.schema(), saveSchema.getSaveValues(domain));
            } else {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(saveSchema.schema(false), Statement.RETURN_GENERATED_KEYS);
                    int index = 1;
                    for (Object value : saveSchema.getSaveValues(domain, false)) {
                        ps.setObject(index++, value);
                    }
                    return ps;
                }, keyHolder);
                schema.getSchemaProperty().setIdValue(domain, keyHolder.getKey());
            }
        }
    }

    @Override
    public <T> T get(Class<T> domainClass, Object id) {
        Schema schema = SchemaContext.getInstance().getSchema(domainClass);
        if (schema != null) {
            SearchSchema searchSchema = schema.searchSchema();
            String sql = searchSchema.schema();
            try {
                return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(domainClass));
            } catch (EmptyResultDataAccessException e) {
                // Handle the case where no results are found or rethrow a custom exception
                return null;
            }
        }
        return null;
    }

    @Override
    public void delete(Class<?> domainClazz, Object id) {
        Schema schema = SchemaContext.getInstance().getSchema(domainClazz);
        if (schema != null) {
            String sql = schema.deleteSchema().schema();
            jdbcTemplate.update(sql, id);
        }
    }

    @Override
    public void update(Object domain) {
        Schema schema = SchemaContext.getInstance().getSchema(domain.getClass());
        if (schema != null) {
            UpdateSchema updateSchema = schema.updateSchema();
            jdbcTemplate.update(updateSchema.schema(), updateSchema.getUpdateValues(domain));
        }
    }


    @Override
    public <T> List<T> find(Class<T> domainClass, String sql, Object... fields) {
        Schema schema = SchemaContext.getInstance().getSchema(domainClass);
        if (schema != null) {
            return jdbcTemplate.query(sql, fields, new BeanPropertyRowMapper<>(domainClass));
        }
        return null;
    }
}