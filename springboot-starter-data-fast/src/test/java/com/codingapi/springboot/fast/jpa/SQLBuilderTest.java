package com.codingapi.springboot.fast.jpa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * SQLBuilder 单元测试（覆盖全部构造器与追加方法）
 */
class SQLBuilderTest {

    @Test
    void constructorWithSqlOnly() {
        SQLBuilder<Object> builder = new SQLBuilder<>("select * from t_demo where 1=1");
        assertEquals("select * from t_demo where 1=1", builder.getSQL());
        assertEquals("select count(1) from select * from t_demo where 1=1", builder.getCountSQL());
        assertEquals(1, builder.getIndex());
        assertNull(builder.getClazz());
        assertEquals(0, builder.getParams().length);
    }

    @Test
    void constructorWithSqlAndCountSql() {
        SQLBuilder<Object> builder = new SQLBuilder<>("select * from t_demo", "select count(1) from t_demo");
        assertEquals("select * from t_demo", builder.getSQL());
        assertEquals("select count(1) from t_demo", builder.getCountSQL());
    }

    @Test
    void constructorWithClassAndSql() {
        SQLBuilder<String> builder = new SQLBuilder<>(String.class, "select name from t_demo");
        assertEquals(String.class, builder.getClazz());
        assertEquals("select name from t_demo", builder.getSQL());
        assertEquals("select count(1) from select name from t_demo", builder.getCountSQL());
    }

    @Test
    void constructorWithClassSqlAndCountSql() {
        SQLBuilder<String> builder = new SQLBuilder<>(String.class, "select name from t_demo", "select count(1) from t_demo");
        assertEquals(String.class, builder.getClazz());
        assertEquals("select name from t_demo", builder.getSQL());
        assertEquals("select count(1) from t_demo", builder.getCountSQL());
    }

    /**
     * append 非空值时拼接带序号的占位符，null 值时忽略
     */
    @Test
    void append() {
        SQLBuilder<Object> builder = new SQLBuilder<>("select * from t_demo where 1=1", "select count(1) from t_demo where 1=1");
        builder.append("and name = ?", "tom");
        assertEquals("select * from t_demo where 1=1 and name = ?1 ", builder.getSQL());
        assertEquals("select count(1) from t_demo where 1=1 and name = ?1 ", builder.getCountSQL());
        assertEquals(2, builder.getIndex());
        assertArrayEquals(new Object[]{"tom"}, builder.getParams());

        // null 值不追加
        builder.append("and id = ?", null);
        assertEquals("select * from t_demo where 1=1 and name = ?1 ", builder.getSQL());
        assertEquals(2, builder.getIndex());
        assertEquals(1, builder.getParams().length);

        builder.append("and sort > ?", 10);
        assertEquals("select * from t_demo where 1=1 and name = ?1  and sort > ?2 ", builder.getSQL());
        assertEquals(3, builder.getIndex());
        assertArrayEquals(new Object[]{"tom", 10}, builder.getParams());
    }

    @Test
    void addParam() {
        SQLBuilder<Object> builder = new SQLBuilder<>("select * from t_demo");
        builder.addParam("a");
        assertEquals(2, builder.getIndex());
        builder.addParam("b", 9);
        assertEquals(9, builder.getIndex());
        assertArrayEquals(new Object[]{"a", "b"}, builder.getParams());
    }

    @Test
    void appendSql() {
        SQLBuilder<Object> builder = new SQLBuilder<>("select * from t_demo", "select count(1) from t_demo");
        builder.appendSql("order by id desc");
        assertEquals("select * from t_demo order by id desc ", builder.getSQL());
        assertEquals("select count(1) from t_demo order by id desc ", builder.getCountSQL());
    }
}
