package com.codingapi.springboot.authorization.jdbc.proxy;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ConnectionProxy 单元测试
 * 验证方法委托以及 prepareStatement/prepareCall 的 SQL 拦截行为
 */
class ConnectionProxyTest {

    private Connection connection;
    private ConnectionProxy proxy;

    @BeforeEach
    void setUp() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        ResultSetProxyTest.registerTestFilter();

        connection = mock(Connection.class);
        proxy = new ConnectionProxy(connection);
    }

    @AfterEach
    void tearDown() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
    }

    @Test
    void testCreateStatementReturnsStatementProxy() throws SQLException {
        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.createStatement(anyInt(), anyInt())).thenReturn(statement);
        when(connection.createStatement(anyInt(), anyInt(), anyInt())).thenReturn(statement);

        assertTrue(proxy.createStatement() instanceof StatementProxy);
        assertTrue(proxy.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY) instanceof StatementProxy);
        assertTrue(proxy.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT) instanceof StatementProxy);

        verify(connection).createStatement();
        verify(connection).createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        verify(connection).createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    @Test
    void testPrepareStatementInterceptsSql() throws SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), (int[]) org.mockito.ArgumentMatchers.any())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), (String[]) org.mockito.ArgumentMatchers.any())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt(), anyInt())).thenReturn(preparedStatement);

        String sql = "select name from t_user";

        assertTrue(proxy.prepareStatement(sql) instanceof PreparedStatementProxy);
        assertTrue(proxy.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) instanceof PreparedStatementProxy);
        assertTrue(proxy.prepareStatement(sql, new int[]{1}) instanceof PreparedStatementProxy);
        assertTrue(proxy.prepareStatement(sql, new String[]{"id"}) instanceof PreparedStatementProxy);
        assertTrue(proxy.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                instanceof PreparedStatementProxy);
        assertTrue(proxy.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT) instanceof PreparedStatementProxy);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testPrepareCallInterceptsSql() throws SQLException {
        CallableStatement callableStatement = mock(CallableStatement.class);
        when(connection.prepareCall(anyString())).thenReturn(callableStatement);
        when(connection.prepareCall(anyString(), anyInt(), anyInt())).thenReturn(callableStatement);
        when(connection.prepareCall(anyString(), anyInt(), anyInt(), anyInt())).thenReturn(callableStatement);

        String sql = "select name from t_user";

        assertTrue(proxy.prepareCall(sql) instanceof CallableStatementProxy);
        assertTrue(proxy.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                instanceof CallableStatementProxy);
        assertTrue(proxy.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT) instanceof CallableStatementProxy);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareCall(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testNativeSqlInterceptsSql() throws SQLException {
        when(connection.nativeSQL(anyString())).thenReturn("native");
        assertEquals("native", proxy.nativeSQL("select name from t_user"));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(connection).nativeSQL(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testTransactionDelegates() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.isClosed()).thenReturn(false);
        when(connection.isReadOnly()).thenReturn(false);
        when(connection.getTransactionIsolation()).thenReturn(Connection.TRANSACTION_READ_COMMITTED);
        SQLWarning warning = mock(SQLWarning.class);
        when(connection.getWarnings()).thenReturn(warning);
        Savepoint savepoint = mock(Savepoint.class);
        when(connection.setSavepoint()).thenReturn(savepoint);
        when(connection.setSavepoint("sp")).thenReturn(savepoint);

        proxy.setAutoCommit(false);
        verify(connection).setAutoCommit(false);
        assertTrue(connection.getAutoCommit());
        assertEquals(true, proxy.getAutoCommit());

        proxy.commit();
        verify(connection).commit();
        proxy.rollback();
        verify(connection).rollback();
        proxy.rollback(savepoint);
        verify(connection).rollback(savepoint);
        proxy.releaseSavepoint(savepoint);
        verify(connection).releaseSavepoint(savepoint);

        proxy.close();
        verify(connection).close();
        assertEquals(false, proxy.isClosed());

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metaData);
        assertSame(metaData, proxy.getMetaData());

        proxy.setReadOnly(true);
        verify(connection).setReadOnly(true);
        assertEquals(false, proxy.isReadOnly());

        proxy.setCatalog("catalog");
        verify(connection).setCatalog("catalog");
        when(connection.getCatalog()).thenReturn("catalog");
        assertEquals("catalog", proxy.getCatalog());

        proxy.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        verify(connection).setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, proxy.getTransactionIsolation());

        assertSame(warning, proxy.getWarnings());
        proxy.clearWarnings();
        verify(connection).clearWarnings();

        assertSame(savepoint, proxy.setSavepoint());
        assertSame(savepoint, proxy.setSavepoint("sp"));
    }

    @Test
    void testTypeMapAndHoldabilityDelegates() throws SQLException {
        Map<String, Class<?>> typeMap = new HashMap<>();
        when(connection.getTypeMap()).thenReturn(typeMap);
        when(connection.getHoldability()).thenReturn(ResultSet.HOLD_CURSORS_OVER_COMMIT);

        assertSame(typeMap, proxy.getTypeMap());
        proxy.setTypeMap(typeMap);
        verify(connection).setTypeMap(typeMap);

        proxy.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        verify(connection).setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, proxy.getHoldability());
    }

    @Test
    void testLobAndMiscDelegates() throws SQLException {
        Clob clob = mock(Clob.class);
        Blob blob = mock(Blob.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        Array array = mock(Array.class);
        Struct struct = mock(Struct.class);

        when(connection.createClob()).thenReturn(clob);
        when(connection.createBlob()).thenReturn(blob);
        when(connection.createNClob()).thenReturn(nClob);
        when(connection.createSQLXML()).thenReturn(sqlxml);
        when(connection.isValid(1)).thenReturn(true);
        when(connection.getClientInfo("k")).thenReturn("v");
        Properties properties = new Properties();
        when(connection.getClientInfo()).thenReturn(properties);
        when(connection.createArrayOf("VARCHAR", new Object[]{"a"})).thenReturn(array);
        when(connection.createStruct("STRUCT", new Object[]{"a"})).thenReturn(struct);
        when(connection.getSchema()).thenReturn("schema");
        when(connection.getNetworkTimeout()).thenReturn(100);

        assertSame(clob, proxy.createClob());
        assertSame(blob, proxy.createBlob());
        assertSame(nClob, proxy.createNClob());
        assertSame(sqlxml, proxy.createSQLXML());
        assertTrue(proxy.isValid(1));

        proxy.setClientInfo("k", "v");
        verify(connection).setClientInfo("k", "v");
        proxy.setClientInfo(properties);
        verify(connection).setClientInfo(properties);
        assertEquals("v", proxy.getClientInfo("k"));
        assertSame(properties, proxy.getClientInfo());

        assertSame(array, proxy.createArrayOf("VARCHAR", new Object[]{"a"}));
        assertSame(struct, proxy.createStruct("STRUCT", new Object[]{"a"}));

        proxy.setSchema("schema");
        verify(connection).setSchema("schema");
        assertEquals("schema", proxy.getSchema());

        Executor executor = mock(Executor.class);
        proxy.abort(executor);
        verify(connection).abort(executor);
        proxy.setNetworkTimeout(executor, 100);
        verify(connection).setNetworkTimeout(executor, 100);
        assertEquals(100, proxy.getNetworkTimeout());

        proxy.beginRequest();
        verify(connection).beginRequest();
        proxy.endRequest();
        verify(connection).endRequest();

        ShardingKey shardingKey = mock(ShardingKey.class);
        ShardingKey superShardingKey = mock(ShardingKey.class);
        when(connection.setShardingKeyIfValid(shardingKey, superShardingKey, 1)).thenReturn(true);
        when(connection.setShardingKeyIfValid(shardingKey, 1)).thenReturn(true);

        assertTrue(proxy.setShardingKeyIfValid(shardingKey, superShardingKey, 1));
        assertTrue(proxy.setShardingKeyIfValid(shardingKey, 1));
        proxy.setShardingKey(shardingKey, superShardingKey);
        verify(connection).setShardingKey(shardingKey, superShardingKey);
        proxy.setShardingKey(shardingKey);
        verify(connection).setShardingKey(shardingKey);
    }

    @Test
    void testWrapperDelegates() throws SQLException {
        when(connection.unwrap(String.class)).thenReturn("unwrapped");
        when(connection.isWrapperFor(String.class)).thenReturn(true);

        assertEquals("unwrapped", proxy.unwrap(String.class));
        assertTrue(proxy.isWrapperFor(String.class));
    }
}
