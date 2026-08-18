package com.codingapi.springboot.fast.jdbc;

import com.codingapi.springboot.fast.jpa.SQLBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * JdbcQuery 单元测试：基于独立的 H2 内存库，不依赖 Spring 上下文，
 * 与其他测试类的数据完全隔离。
 */
class JdbcQueryUnitTest {

    /**
     * 查询结果行映射 Bean（列 user_name 映射到 userName）
     */
    public static class JqRow {
        private Integer id;
        private String userName;
        private Integer sort;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }

    private JdbcTemplate jdbcTemplate;
    private JdbcQuery jdbcQuery;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:jdbc_query_unit;DB_CLOSE_DELAY=-1");
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("drop table if exists jq_demo");
        jdbcTemplate.execute("create table jq_demo(id int primary key, user_name varchar(50), sort int)");
        jdbcTemplate.update("insert into jq_demo(id, user_name, sort) values (?, ?, ?)", 1, "alice", 10);
        jdbcTemplate.update("insert into jq_demo(id, user_name, sort) values (?, ?, ?)", 2, "bob", 20);
        jdbcQuery = new JdbcQuery(jdbcTemplate);
    }

    /**
     * Map 查询，验证下划线列名转驼峰
     */
    @Test
    void queryForMapList() {
        List<Map<String, Object>> list = jdbcQuery.queryForMapList("select * from jq_demo order by id");
        assertEquals(2, list.size());
        assertEquals("alice", list.get(0).get("userName"));
        assertEquals("bob", list.get(1).get("userName"));
        assertTrue(list.get(0).containsKey("id"));
        assertTrue(list.get(0).containsKey("sort"));
    }

    @Test
    void queryForMapListWithParams() {
        List<Map<String, Object>> list = jdbcQuery.queryForMapList(
                "select * from jq_demo where sort > ?", 15);
        assertEquals(1, list.size());
        assertEquals("bob", list.get(0).get("userName"));
    }

    @Test
    void queryForMapListWithBuilder() {
        SQLBuilder<Object> builder = new SQLBuilder<>(
                "select * from jq_demo where 1=1", "select count(1) from jq_demo where 1=1");
        builder.append("and id = ?", 1);
        List<Map<String, Object>> list = jdbcQuery.queryForMapList(builder);
        assertEquals(1, list.size());
        assertEquals("alice", list.get(0).get("userName"));
    }

    /**
     * Bean 查询
     */
    @Test
    void queryForList() {
        List<JqRow> list = jdbcQuery.queryForList(
                "select * from jq_demo where id = ?", JqRow.class, 2);
        assertEquals(1, list.size());
        assertEquals("bob", list.get(0).getUserName());
        assertEquals(Integer.valueOf(20), list.get(0).getSort());
    }

    @Test
    void queryForListWithBuilder() {
        SQLBuilder<JqRow> builder = new SQLBuilder<>(
                JqRow.class, "select * from jq_demo where 1=1", "select count(1) from jq_demo where 1=1");
        builder.append("and sort <= ?", 10);
        List<JqRow> list = jdbcQuery.queryForList(builder);
        assertEquals(1, list.size());
        assertEquals("alice", list.get(0).getUserName());
    }

    /**
     * 分页查询（显式 count SQL）
     */
    @Test
    void queryForPageWithCountSql() {
        Page<JqRow> page = jdbcQuery.queryForPage(
                "select * from jq_demo where sort > ?",
                "select count(1) from jq_demo where sort > ?",
                JqRow.class, PageRequest.of(0, 10), 5);
        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    @Test
    void queryForPageWithBuilder() {
        SQLBuilder<JqRow> builder = new SQLBuilder<>(
                JqRow.class, "select * from jq_demo where 1=1", "select count(1) from jq_demo where 1=1");
        builder.append("and sort > ?", 15);
        Page<JqRow> page = jdbcQuery.queryForPage(builder, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("bob", page.getContent().get(0).getUserName());
    }

    /**
     * Map 分页查询（显式 count SQL）
     */
    @Test
    void queryForMapPageWithCountSql() {
        Page<Map<String, Object>> page = jdbcQuery.queryForMapPage(
                "select * from jq_demo where sort > ?",
                "select count(1) from jq_demo where sort > ?",
                PageRequest.of(0, 10), 5);
        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    @Test
    void queryForMapPageWithBuilder() {
        SQLBuilder<Object> builder = new SQLBuilder<>(
                "select * from jq_demo where 1=1", "select count(1) from jq_demo where 1=1");
        builder.append("and id = ?", 2);
        Page<Map<String, Object>> page = jdbcQuery.queryForMapPage(builder, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("bob", page.getContent().get(0).get("userName"));
    }

    /**
     * count SQL 占位符数量少于查询参数时，内部 arraycopy 截断参数
     */
    @Test
    void queryForMapPageTruncatesParamsForCountSql() {
        Page<Map<String, Object>> page = jdbcQuery.queryForMapPage(
                "select * from jq_demo where sort > ?",
                "select count(1) from jq_demo",
                PageRequest.of(0, 10), 5);
        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    /**
     * 自动拼接 count SQL 的两个方法：countSql = "SELECT COUNT(1) " + sql，
     * 要求 sql 以 from 开头。原生 SQL 无法同时满足两种形态，
     * 因此用 mock 的 JdbcTemplate 验证其 count SQL 拼装与调用逻辑。
     */
    @Test
    void queryForPageAutoCountSql() {
        JqRow row = new JqRow();
        row.setId(1);
        row.setUserName("alice");
        List<JqRow> rows = new ArrayList<>();
        rows.add(row);
        // 空参数数组的 varargs 调用无法被 Mockito 桩匹配, 改用 default answer 直接返回数据
        JdbcTemplate mockTemplate = mock(JdbcTemplate.class, invocation -> {
            String methodName = invocation.getMethod().getName();
            if ("query".equals(methodName)) {
                return rows;
            }
            if ("queryForObject".equals(methodName)) {
                return 1L;
            }
            return null;
        });

        JdbcQuery query = new JdbcQuery(mockTemplate);
        Page<JqRow> page = query.queryForPage("from jq_demo", JqRow.class, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        verify(mockTemplate).queryForObject(eq("SELECT COUNT(1) from jq_demo"), eq(Long.class));
    }

    @Test
    void queryForMapPageAutoCountSql() {
        JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
        List<Map<String, Object>> rows = new ArrayList<>();
        doReturn(rows).when(mockTemplate).query(anyString(), any(RowMapper.class), (Object[]) any());
        // 自动 count SQL 无占位符, countQuery 以空参数数组调用; 不带 varargs 参数的桩恰好匹配零长 varargs 调用
        doReturn(0L).when(mockTemplate).queryForObject(anyString(), eq(Long.class));

        JdbcQuery query = new JdbcQuery(mockTemplate);
        Page<Map<String, Object>> page = query.queryForMapPage("from jq_demo", PageRequest.of(0, 10));

        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
        verify(mockTemplate).queryForObject(eq("SELECT COUNT(1) from jq_demo"), eq(Long.class));
    }
}
