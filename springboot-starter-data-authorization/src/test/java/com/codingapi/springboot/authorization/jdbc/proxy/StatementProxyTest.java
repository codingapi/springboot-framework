package com.codingapi.springboot.authorization.jdbc.proxy;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * StatementProxy 单元测试
 * 验证方法委托以及 SQL 拦截行为
 */
class StatementProxyTest {

    private Statement statement;
    private StatementProxy proxy;

    @BeforeEach
    void setUp() throws SQLException {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        ResultSetProxyTest.registerTestFilter();

        statement = mock(Statement.class);
        proxy = new StatementProxy(statement, SQLExecuteState.unIntercept("select 1"));
    }

    @AfterEach
    void tearDown() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
    }

    private ResultSet mockEmptyResultSet() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(0);
        return resultSet;
    }

    @Test
    void testExecuteQueryInterceptsSql() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        ResultSet result = proxy.executeQuery("select name from t_user");

        assertTrue(result instanceof ResultSetProxy);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(statement).executeQuery(captor.capture());
        // SQL 被注入了行权限条件
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testExecuteQueryWithoutInterception() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        // t_other 表未配置权限条件, 不会注入过滤条件; 但 SQL 仍会经 JSqlParser 解析并重新序列化(大小写可能变化)
        proxy.executeQuery("select name from t_other");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(statement).executeQuery(captor.capture());
        assertEquals("SELECT name FROM t_other", captor.getValue());
    }

    @Test
    void testExecuteUpdateVariantsInterceptSql() throws SQLException {
        when(statement.executeUpdate(anyString())).thenReturn(1);
        when(statement.executeUpdate(anyString(), anyInt())).thenReturn(2);
        when(statement.executeUpdate(anyString(), (int[]) org.mockito.ArgumentMatchers.any())).thenReturn(3);
        when(statement.executeUpdate(anyString(), (String[]) org.mockito.ArgumentMatchers.any())).thenReturn(4);
        when(statement.executeLargeUpdate(anyString())).thenReturn(5L);
        when(statement.executeLargeUpdate(anyString(), anyInt())).thenReturn(6L);
        when(statement.executeLargeUpdate(anyString(), (int[]) org.mockito.ArgumentMatchers.any())).thenReturn(7L);
        when(statement.executeLargeUpdate(anyString(), (String[]) org.mockito.ArgumentMatchers.any())).thenReturn(8L);

        String sql = "select name from t_user";
        assertEquals(1, proxy.executeUpdate(sql));
        assertEquals(2, proxy.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS));
        assertEquals(3, proxy.executeUpdate(sql, new int[]{1}));
        assertEquals(4, proxy.executeUpdate(sql, new String[]{"id"}));
        assertEquals(5L, proxy.executeLargeUpdate(sql));
        assertEquals(6L, proxy.executeLargeUpdate(sql, Statement.RETURN_GENERATED_KEYS));
        assertEquals(7L, proxy.executeLargeUpdate(sql, new int[]{1}));
        assertEquals(8L, proxy.executeLargeUpdate(sql, new String[]{"id"}));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(statement).executeUpdate(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testExecuteVariantsInterceptSql() throws SQLException {
        when(statement.execute(anyString())).thenReturn(true);
        when(statement.execute(anyString(), anyInt())).thenReturn(true);
        when(statement.execute(anyString(), (int[]) org.mockito.ArgumentMatchers.any())).thenReturn(true);
        when(statement.execute(anyString(), (String[]) org.mockito.ArgumentMatchers.any())).thenReturn(true);

        String sql = "select name from t_user";
        assertTrue(proxy.execute(sql));
        assertTrue(proxy.execute(sql, Statement.RETURN_GENERATED_KEYS));
        assertTrue(proxy.execute(sql, new int[]{1}));
        assertTrue(proxy.execute(sql, new String[]{"id"}));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(statement).execute(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testAddBatchInterceptsSql() throws SQLException {
        proxy.addBatch("select name from t_user");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(statement).addBatch(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testResultSetWrapping() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        ResultSet generatedKeys = mockEmptyResultSet();
        when(statement.getResultSet()).thenReturn(resultSet);
        when(statement.getGeneratedKeys()).thenReturn(generatedKeys);

        assertTrue(proxy.getResultSet() instanceof ResultSetProxy);
        assertTrue(proxy.getGeneratedKeys() instanceof ResultSetProxy);
        verify(statement).getResultSet();
        verify(statement).getGeneratedKeys();
    }

    @Test
    void testGetConnectionReturnsConnectionProxy() throws SQLException {
        Connection connection = mock(Connection.class);
        when(statement.getConnection()).thenReturn(connection);
        assertTrue(proxy.getConnection() instanceof ConnectionProxy);
    }

    @Test
    void testAttributeDelegates() throws SQLException {
        when(statement.getMaxFieldSize()).thenReturn(1);
        when(statement.getMaxRows()).thenReturn(2);
        when(statement.getQueryTimeout()).thenReturn(3);
        SQLWarning warning = mock(SQLWarning.class);
        when(statement.getWarnings()).thenReturn(warning);
        when(statement.getUpdateCount()).thenReturn(4);
        when(statement.getMoreResults()).thenReturn(true);
        when(statement.getFetchDirection()).thenReturn(ResultSet.FETCH_FORWARD);
        when(statement.getFetchSize()).thenReturn(5);
        when(statement.getResultSetConcurrency()).thenReturn(ResultSet.CONCUR_READ_ONLY);
        when(statement.getResultSetType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        when(statement.getMoreResults(Statement.CLOSE_CURRENT_RESULT)).thenReturn(false);
        when(statement.getResultSetHoldability()).thenReturn(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        when(statement.isClosed()).thenReturn(false);
        when(statement.isPoolable()).thenReturn(true);
        when(statement.isCloseOnCompletion()).thenReturn(false);
        when(statement.getLargeUpdateCount()).thenReturn(6L);
        when(statement.getLargeMaxRows()).thenReturn(7L);
        when(statement.executeBatch()).thenReturn(new int[]{1, 2});
        when(statement.executeLargeBatch()).thenReturn(new long[]{3L, 4L});

        assertEquals(1, proxy.getMaxFieldSize());
        assertEquals(2, proxy.getMaxRows());
        assertEquals(3, proxy.getQueryTimeout());
        assertSame(warning, proxy.getWarnings());
        assertEquals(4, proxy.getUpdateCount());
        assertTrue(proxy.getMoreResults());
        assertEquals(ResultSet.FETCH_FORWARD, proxy.getFetchDirection());
        assertEquals(5, proxy.getFetchSize());
        assertEquals(ResultSet.CONCUR_READ_ONLY, proxy.getResultSetConcurrency());
        assertEquals(ResultSet.TYPE_FORWARD_ONLY, proxy.getResultSetType());
        assertEquals(false, proxy.getMoreResults(Statement.CLOSE_CURRENT_RESULT));
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, proxy.getResultSetHoldability());
        assertEquals(false, proxy.isClosed());
        assertTrue(proxy.isPoolable());
        assertEquals(false, proxy.isCloseOnCompletion());
        assertEquals(6L, proxy.getLargeUpdateCount());
        assertEquals(7L, proxy.getLargeMaxRows());
        assertEquals(2, proxy.executeBatch().length);
        assertEquals(2, proxy.executeLargeBatch().length);

        proxy.setMaxFieldSize(10);
        proxy.setMaxRows(20);
        proxy.setEscapeProcessing(true);
        proxy.setQueryTimeout(30);
        proxy.setFetchDirection(ResultSet.FETCH_REVERSE);
        proxy.setFetchSize(40);
        proxy.setPoolable(false);
        proxy.setLargeMaxRows(50L);
        proxy.clearWarnings();
        proxy.clearBatch();
        proxy.closeOnCompletion();
        proxy.close();
        proxy.cancel();
        proxy.setCursorName("cursor");

        verify(statement).setMaxFieldSize(10);
        verify(statement).setMaxRows(20);
        verify(statement).setEscapeProcessing(true);
        verify(statement).setQueryTimeout(30);
        verify(statement).setFetchDirection(ResultSet.FETCH_REVERSE);
        verify(statement).setFetchSize(40);
        verify(statement).setPoolable(false);
        verify(statement).setLargeMaxRows(50L);
        verify(statement).clearWarnings();
        verify(statement).clearBatch();
        verify(statement).closeOnCompletion();
        verify(statement).close();
        verify(statement).cancel();
        verify(statement).setCursorName("cursor");
    }

    @Test
    void testEnquoteAndWrapperDelegates() throws SQLException {
        when(statement.enquoteLiteral("v")).thenReturn("'v'");
        when(statement.enquoteIdentifier("id", true)).thenReturn("\"id\"");
        when(statement.isSimpleIdentifier("id")).thenReturn(true);
        when(statement.enquoteNCharLiteral("v")).thenReturn("N'v'");
        when(statement.unwrap(String.class)).thenReturn("unwrapped");
        when(statement.isWrapperFor(String.class)).thenReturn(true);

        assertEquals("'v'", proxy.enquoteLiteral("v"));
        assertEquals("\"id\"", proxy.enquoteIdentifier("id", true));
        assertTrue(proxy.isSimpleIdentifier("id"));
        assertEquals("N'v'", proxy.enquoteNCharLiteral("v"));
        assertEquals("unwrapped", proxy.unwrap(String.class));
        assertTrue(proxy.isWrapperFor(String.class));
    }
}
