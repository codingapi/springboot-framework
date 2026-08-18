package com.codingapi.springboot.fast.jpa.map;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * QueryColumns / QueryColumnsContext 单元测试
 */
class QueryColumnsTest {

    /**
     * 列别名解析：覆盖 小写as、大写AS、表前缀(.)、普通列 四种分支
     */
    @Test
    void getColumnAlias() {
        QueryColumns columns = QueryColumnsContext.build(
                "u.id as iii",
                "u.name AS userName",
                "t_demo.sort",
                "plain_column",
                "  spaced  "
        );
        List<String> aliases = columns.getColumnAlias();
        assertEquals(Arrays.asList("iii", "userName", "sort", "plain_column", "spaced"), aliases);
        QueryColumnsContext.getInstance().clearCache(columns.getKey());
    }

    /**
     * 多个 as/AS 时取最后一段
     */
    @Test
    void getColumnAliasWithMultipleAs() {
        QueryColumns columns = QueryColumnsContext.build("concat(a,b) as x as y");
        List<String> aliases = columns.getColumnAlias();
        assertEquals(1, aliases.size());
        assertEquals("y", aliases.get(0));
        QueryColumnsContext.getInstance().clearCache(columns.getKey());
    }

    @Test
    void getColumnSql() {
        QueryColumns columns = QueryColumnsContext.build("u.id as iii", "u.name");
        assertEquals("u.id as iii,u.name", columns.getColumnSql());
        assertEquals(2, columns.getColumns().size());
        QueryColumnsContext.getInstance().clearCache(columns.getKey());
    }

    /**
     * addColumn 支持链式调用
     */
    @Test
    void addColumnChaining() {
        QueryColumns columns = new QueryColumns();
        assertNotNull(columns.getKey());
        assertEquals(8, columns.getKey().length());
        QueryColumns same = columns.addColumn("a").addColumn("b");
        assertSame(columns, same);
        assertEquals("a,b", columns.getColumnSql());
    }

    /**
     * context 注册、查询与清理
     */
    @Test
    void contextBuildAndClear() {
        QueryColumns columns = QueryColumnsContext.build("id");
        String key = columns.getKey();
        assertSame(columns, QueryColumnsContext.getInstance().getQueryColumns(key));
        QueryColumnsContext.getInstance().clearCache(key);
        assertNull(QueryColumnsContext.getInstance().getQueryColumns(key));
    }

    /**
     * 每次 build 生成的 key 不同，互不影响
     */
    @Test
    void contextKeysAreIndependent() {
        QueryColumns c1 = QueryColumnsContext.build("a");
        QueryColumns c2 = QueryColumnsContext.build("b");
        assertNotNull(QueryColumnsContext.getInstance().getQueryColumns(c1.getKey()));
        assertNotNull(QueryColumnsContext.getInstance().getQueryColumns(c2.getKey()));
        QueryColumnsContext.getInstance().clearCache(c1.getKey());
        QueryColumnsContext.getInstance().clearCache(c2.getKey());
    }
}
