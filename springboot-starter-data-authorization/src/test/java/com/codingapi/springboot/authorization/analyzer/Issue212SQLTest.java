package com.codingapi.springboot.authorization.analyzer;

import com.codingapi.springboot.authorization.enhancer.DataPermissionSQLEnhancer;
import com.codingapi.springboot.authorization.enhancer.TableColumnAliasContext;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GitHub Issue #212 复现测试
 * <p>
 * 如下SQL无法正常的提取到表名和字段数据信息（原始SQL见 test resources issue212.sql）：
 * 外层为分页包装的子查询（FROM ( ... ) AS __base__），内层为多表 LEFT JOIN，
 * 且 JOIN 对象包含 ROW_NUMBER() OVER (PARTITION BY ...) 窗口函数派生表、
 * CAST、CASE WHEN、ORDER BY ... NULLS LAST 等复杂表达式。
 */
class Issue212SQLTest {

    private static final String SQL = loadResource("issue212.sql");

    private static String loadResource(String name) {
        try (InputStream in = Issue212SQLTest.class.getClassLoader().getResourceAsStream(name)) {
            assertNotNull(in, "测试资源缺失: " + name);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 对员工基础信息表注入行权限条件：%s.unit_id = 1000
     */
    private static RowHandler rowHandler() {
        return (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO")) {
                return Condition.formatCondition("%s.unit_id = 1000", tableAlias);
            }
            return null;
        };
    }

    @Test
    void complexSQLShouldBeParsed() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        assertNotNull(builder.getTableAlias(), "表别名上下文不应为空");
    }

    @Test
    void shouldExtractAllPhysicalTables() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        builder.getNewSQL();
        TableColumnAliasContext context = builder.getTableAlias();
        System.out.println("tableAlias = " + context.getTableAlias());

        // 嵌套 LEFT JOIN 子查询中的物理表都应被提取到
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO", context.getTableName("emp"));
        assertEquals("BIZ_PBM_ORGANIZATION", context.getTableName("org"));
        assertEquals("BIZ_PBM_POST", context.getTableName("post"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_EDUCATION_HIGHEST", context.getTableName("e"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_TECHNOLOGY_HIGHEST", context.getTableName("t"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_SKILL_HIGHEST", context.getTableName("s"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_POSITION", context.getTableName("p"));
    }

    @Test
    void shouldInjectPermissionConditionIntoNestedTable() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        String newSql = builder.getNewSQL();
        System.out.println("newSql = " + newSql);

        // 权限条件必须被注入到内层 emp 表的 WHERE 中
        assertTrue(newSql.contains("emp.unit_id = 1000"),
                "行权限条件未注入到嵌套子查询的 emp 表，提取/增强失败");
    }

    @Test
    void shouldResolveDerivedColumnsOnIssueSQL() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        builder.getNewSQL();
        TableColumnAliasContext context = builder.getTableAlias();

        // 元数据表名为派生别名 __base__（H2 等）或空串（PG，issue 截图现场）时，
        // 裸列应能沿多层派生链归因到物理表字段：

        // emp.* 单星展开归因
        assertArrayEquals(new String[]{"T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO", "card_no"},
                context.resolveTableNameAndColumn("__base__", "card_no"));
        // 二级派生链：curpos -> c -> p
        assertArrayEquals(new String[]{"T_DYNAMIC_BIZ_PBM_EMP_POSITION", "position_hierarchy_code"},
                context.resolveTableNameAndColumn("", "position_hierarchy_code"));
        // 一层派生：skill 子查询
        assertArrayEquals(new String[]{"T_DYNAMIC_BIZ_PBM_EMP_SKILL_HIGHEST", "highest_skill_name"},
                context.resolveTableNameAndColumn("__BASE__", "highest_skill_name"));
        // 显式别名投影：org.SYSTEM_CODE AS orgCode
        assertArrayEquals(new String[]{"BIZ_PBM_ORGANIZATION", "SYSTEM_CODE"},
                context.resolveTableNameAndColumn("", "orgCode"));
        // JOIN 直连表：post.post_type_code
        assertArrayEquals(new String[]{"BIZ_PBM_POST", "post_type_code"},
                context.resolveTableNameAndColumn("__base__", "post_type_code"));
        // 多义列（多个派生表均含 employee_id 且归属不同物理表）：保守回退，不误归因
        assertArrayEquals(new String[]{"", "employee_id"},
                context.resolveTableNameAndColumn("", "employee_id"));
    }
}
