package com.codingapi.springboot.authorization.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SQLUtils 单元测试
 */
class SQLUtilsTest {

    @Test
    void testIsQuerySqlWithSelect() {
        assertTrue(SQLUtils.isQuerySql("select * from t_user"));
        assertTrue(SQLUtils.isQuerySql("SELECT id, name FROM t_user WHERE id = 1"));
    }

    @Test
    void testIsQuerySqlWithNonSelect() {
        assertFalse(SQLUtils.isQuerySql("update t_user set name = 'x'"));
        assertFalse(SQLUtils.isQuerySql("delete from t_user"));
        assertFalse(SQLUtils.isQuerySql("insert into t_user(name) values('x')"));
    }

    @Test
    void testIsQuerySqlWithNullOrBlank() {
        assertFalse(SQLUtils.isQuerySql(null));
        assertFalse(SQLUtils.isQuerySql(""));
        assertFalse(SQLUtils.isQuerySql("   "));
    }

    @Test
    void testIsQuerySqlWithInvalidSql() {
        assertFalse(SQLUtils.isQuerySql("this is not a sql"));
    }
}
