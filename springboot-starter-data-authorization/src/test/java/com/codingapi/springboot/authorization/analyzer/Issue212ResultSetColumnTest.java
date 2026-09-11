package com.codingapi.springboot.authorization.analyzer;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.enhancer.DataPermissionSQLEnhancer;
import com.codingapi.springboot.authorization.filter.DataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;
import com.codingapi.springboot.authorization.jdbc.proxy.ResultSetProxy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GitHub Issue #212 复现测试（ResultSet 列元数据 → 物理表/字段解析）。
 * <p>
 * 场景：查询引擎将业务 SQL 包装为分页 SQL
 * <pre>SELECT ... FROM ( ... ) AS __base__ WHERE 1 = 1 LIMIT ? OFFSET ?</pre>
 * 此时候选结果集列的 ResultSetMetaData.getTableName() 不再是物理表名
 * （为空串或派生表别名），列又没有表限定符，导致框架无法把结果列
 * （如 CARD_NO）映射回物理表 T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO.card_no，
 * DataAuthorizationFilter.supportColumnAuthorization 收到 tableName=""，
 * 列权限/脱敏失效（见 issue 截图调试现场）。
 */
class Issue212ResultSetColumnTest {

    private static final String EMP_TABLE = "T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO";
    private static final String ID_CARD = "522101196612113210";

    /**
     * 与 issue #212 相同结构的分页包装 SQL（列做了裁剪，结构与问题一致：
     * 外层裸列名投影 + FROM 派生表 AS __base__ + LIMIT ? OFFSET ?）
     */
    private static final String WRAPPER_SQL = """
            SELECT
              id,
              unit_name,
              card_no
            FROM
              (
                SELECT
                  emp.*,
                  org.SYSTEM_CODE AS orgCode
                FROM
                  T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO emp
                  LEFT JOIN BIZ_PBM_ORGANIZATION org ON org.id = emp.department_id
                WHERE
                  emp.sys_deleted = 0
              ) AS __base__
            WHERE
              1 = 1
            LIMIT
              ?
            OFFSET
              ?
            """;

    /** 记录框架回调 DataAuthorizationFilter 时传入的表名/列名 */
    static class RecordingFilter implements DataAuthorizationFilter {

        final List<String[]> invocations = new ArrayList<>();

        @Override
        public <T> T columnAuthorization(String tableName, String columnName, T value) {
            return value;
        }

        @Override
        public Condition rowAuthorization(String tableName, String tableAlias) {
            return null;
        }

        @Override
        public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
            invocations.add(new String[]{tableName, columnName});
            return false;
        }

        @Override
        public boolean supportRowAuthorization(String tableName, String tableAlias) {
            return false;
        }
    }

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:issue212_rs;DB_CLOSE_DELAY=-1");
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE " + EMP_TABLE + " (" +
                    "id BIGINT PRIMARY KEY," +
                    "unit_name VARCHAR(64)," +
                    "card_no VARCHAR(32)," +
                    "department_id BIGINT," +
                    "sys_deleted INT)");
            statement.execute("CREATE TABLE BIZ_PBM_ORGANIZATION (" +
                    "id BIGINT PRIMARY KEY," +
                    "SYSTEM_CODE VARCHAR(32)," +
                    "tree_sort VARCHAR(64))");
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO " + EMP_TABLE + " VALUES (1, '单位A', ?, 10, 0)")) {
                ps.setString(1, ID_CARD);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO BIZ_PBM_ORGANIZATION VALUES (10, 'ORG001', '1')")) {
                ps.executeUpdate();
            }
        }
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:issue212_rs;DB_CLOSE_DELAY=-1");
             Statement st = conn.createStatement()) {
            st.execute("DROP ALL OBJECTS");
        }
    }

    @Test
    void shouldResolvePhysicalTableAndColumnForPagingWrappedSQL() throws Exception {
        // 1. 走框架真实链路：增强器提取表/字段别名 → SQLExecuteState → ResultSetProxy
        DataPermissionSQLEnhancer enhancer =
                new DataPermissionSQLEnhancer(WRAPPER_SQL, (subSql, tableName, tableAlias) -> null);
        String newSql = enhancer.getNewSQL();
        SQLExecuteState executeState = SQLExecuteState.intercept(WRAPPER_SQL, newSql, enhancer.getTableAlias());

        RecordingFilter filter = new RecordingFilter();
        DataAuthorizationContext context = DataAuthorizationContext.getInstance();
        context.addDataAuthorizationFilter(filter);
        try {
            try (PreparedStatement ps = connection.prepareStatement(executeState.getSql())) {
                ps.setInt(1, 10);
                ps.setInt(2, 0);
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetProxy proxy = new ResultSetProxy(rs, executeState);
                    assertTrue(proxy.next(), "应查询到测试数据");

                    // 读取 CARD_NO 列（触发列权限回调）
                    String value = proxy.getString("card_no");
                    assertEquals(ID_CARD, value);
                }
            }
        } finally {
            context.clearDataAuthorizationFilters();
        }

        assertFalse(filter.invocations.isEmpty(), "列权限回调未触发");
        String[] cardNoInvocation = filter.invocations.stream()
                .filter(inv -> inv[1] != null && inv[1].equalsIgnoreCase("card_no"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未捕获到 card_no 列回调: "
                        + filter.invocations.stream().map(inv -> inv[0] + "." + inv[1]).toList()));

        // 2. 期望：框架应将派生表 __base__ 的裸列解析回物理表/字段
        //    （issue 现场为 tableName=""，列脱敏/权限因此失效）
        assertEquals(EMP_TABLE, cardNoInvocation[0],
                "Issue #212: 分页包装 SQL 的结果列未能解析出物理表名");
        assertTrue("card_no".equalsIgnoreCase(cardNoInvocation[1]),
                "Issue #212: 分页包装 SQL 的结果列未能解析出物理字段名, 实际: " + cardNoInvocation[1]);
    }
}
