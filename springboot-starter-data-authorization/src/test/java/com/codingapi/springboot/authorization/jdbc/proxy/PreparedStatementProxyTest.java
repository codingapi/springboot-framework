package com.codingapi.springboot.authorization.jdbc.proxy;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PreparedStatementProxy 单元测试
 * 验证方法委托以及 SQL 拦截行为
 */
class PreparedStatementProxyTest {

    private PreparedStatement preparedStatement;
    private PreparedStatementProxy proxy;

    @BeforeEach
    void setUp() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        ResultSetProxyTest.registerTestFilter();

        preparedStatement = mock(PreparedStatement.class);
        proxy = new PreparedStatementProxy(preparedStatement, SQLExecuteState.unIntercept("select 1"));
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
    void testExecuteMethods() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeLargeUpdate()).thenReturn(2L);

        assertTrue(proxy.executeQuery() instanceof ResultSetProxy);
        assertEquals(1, proxy.executeUpdate());
        assertTrue(proxy.execute());
        assertEquals(2L, proxy.executeLargeUpdate());

        verify(preparedStatement).executeQuery();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).execute();
        verify(preparedStatement).executeLargeUpdate();
    }

    @Test
    void testParameterSettersDelegate() throws SQLException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1});
        Reader reader = new StringReader("x");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(1000L);
        Time time = new Time(2000L);
        Timestamp timestamp = new Timestamp(3000L);
        Ref ref = mock(Ref.class);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        Array array = mock(Array.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        RowId rowId = mock(RowId.class);
        URL url = toUrl();

        proxy.setNull(1, java.sql.Types.VARCHAR);
        proxy.setBoolean(1, true);
        proxy.setByte(1, (byte) 1);
        proxy.setShort(1, (short) 1);
        proxy.setInt(1, 1);
        proxy.setLong(1, 1L);
        proxy.setFloat(1, 1.0f);
        proxy.setDouble(1, 1.0d);
        proxy.setBigDecimal(1, BigDecimal.ONE);
        proxy.setString(1, "x");
        proxy.setBytes(1, new byte[]{1});
        proxy.setDate(1, date);
        proxy.setTime(1, time);
        proxy.setTimestamp(1, timestamp);
        proxy.setAsciiStream(1, inputStream, 1);
        proxy.setUnicodeStream(1, inputStream, 1);
        proxy.setBinaryStream(1, inputStream, 1);
        proxy.clearParameters();
        proxy.setObject(1, "x", java.sql.Types.VARCHAR);
        proxy.setObject(1, "x");
        proxy.setCharacterStream(1, reader, 1);
        proxy.setRef(1, ref);
        proxy.setBlob(1, blob);
        proxy.setClob(1, clob);
        proxy.setArray(1, array);
        proxy.setDate(1, date, calendar);
        proxy.setTime(1, time, calendar);
        proxy.setTimestamp(1, timestamp, calendar);
        proxy.setNull(1, java.sql.Types.VARCHAR, "VARCHAR");
        proxy.setURL(1, url);
        proxy.setRowId(1, rowId);
        proxy.setNString(1, "x");
        proxy.setNCharacterStream(1, reader, 1L);
        proxy.setNClob(1, nClob);
        proxy.setClob(1, reader, 1L);
        proxy.setBlob(1, inputStream, 1L);
        proxy.setNClob(1, reader, 1L);
        proxy.setSQLXML(1, sqlxml);
        proxy.setObject(1, "x", java.sql.Types.VARCHAR, 1);
        proxy.setAsciiStream(1, inputStream, 1L);
        proxy.setBinaryStream(1, inputStream, 1L);
        proxy.setCharacterStream(1, reader, 1L);
        proxy.setAsciiStream(1, inputStream);
        proxy.setBinaryStream(1, inputStream);
        proxy.setCharacterStream(1, reader);
        proxy.setNCharacterStream(1, reader);
        proxy.setClob(1, reader);
        proxy.setBlob(1, inputStream);
        proxy.setNClob(1, reader);
        SQLType sqlType = JDBCType.VARCHAR;
        proxy.setObject(1, "x", sqlType, 1);
        proxy.setObject(1, "x", sqlType);
        proxy.addBatch();

        verify(preparedStatement).setNull(1, java.sql.Types.VARCHAR);
        verify(preparedStatement).setBoolean(1, true);
        verify(preparedStatement).setByte(1, (byte) 1);
        verify(preparedStatement).setShort(1, (short) 1);
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).setLong(1, 1L);
        verify(preparedStatement).setFloat(1, 1.0f);
        verify(preparedStatement).setDouble(1, 1.0d);
        verify(preparedStatement).setBigDecimal(1, BigDecimal.ONE);
        verify(preparedStatement).setString(1, "x");
        verify(preparedStatement).setDate(1, date);
        verify(preparedStatement).setTime(1, time);
        verify(preparedStatement).setTimestamp(1, timestamp);
        verify(preparedStatement).setAsciiStream(1, inputStream, 1);
        verify(preparedStatement).setUnicodeStream(1, inputStream, 1);
        verify(preparedStatement).setBinaryStream(1, inputStream, 1);
        verify(preparedStatement).clearParameters();
        verify(preparedStatement).setObject(1, "x", java.sql.Types.VARCHAR);
        verify(preparedStatement).setObject(1, "x");
        verify(preparedStatement).setCharacterStream(1, reader, 1);
        verify(preparedStatement).setRef(1, ref);
        verify(preparedStatement).setBlob(1, blob);
        verify(preparedStatement).setClob(1, clob);
        verify(preparedStatement).setArray(1, array);
        verify(preparedStatement).setDate(1, date, calendar);
        verify(preparedStatement).setTime(1, time, calendar);
        verify(preparedStatement).setTimestamp(1, timestamp, calendar);
        verify(preparedStatement).setNull(1, java.sql.Types.VARCHAR, "VARCHAR");
        verify(preparedStatement).setURL(1, url);
        verify(preparedStatement).setRowId(1, rowId);
        verify(preparedStatement).setNString(1, "x");
        verify(preparedStatement).setNCharacterStream(1, reader, 1L);
        verify(preparedStatement).setNClob(1, nClob);
        verify(preparedStatement).setClob(1, reader, 1L);
        verify(preparedStatement).setBlob(1, inputStream, 1L);
        verify(preparedStatement).setNClob(1, reader, 1L);
        verify(preparedStatement).setSQLXML(1, sqlxml);
        verify(preparedStatement).setObject(1, "x", java.sql.Types.VARCHAR, 1);
        verify(preparedStatement).setAsciiStream(1, inputStream, 1L);
        verify(preparedStatement).setBinaryStream(1, inputStream, 1L);
        verify(preparedStatement).setCharacterStream(1, reader, 1L);
        verify(preparedStatement).setAsciiStream(1, inputStream);
        verify(preparedStatement).setBinaryStream(1, inputStream);
        verify(preparedStatement).setCharacterStream(1, reader);
        verify(preparedStatement).setNCharacterStream(1, reader);
        verify(preparedStatement).setClob(1, reader);
        verify(preparedStatement).setBlob(1, inputStream);
        verify(preparedStatement).setNClob(1, reader);
        verify(preparedStatement).setObject(1, "x", sqlType, 1);
        verify(preparedStatement).setObject(1, "x", sqlType);
        verify(preparedStatement).addBatch();
    }

    @Test
    void testMetaDataDelegates() throws SQLException {
        ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
        ParameterMetaData parameterMetaData = mock(ParameterMetaData.class);
        when(preparedStatement.getMetaData()).thenReturn(resultSetMetaData);
        when(preparedStatement.getParameterMetaData()).thenReturn(parameterMetaData);

        assertSame(resultSetMetaData, proxy.getMetaData());
        assertSame(parameterMetaData, proxy.getParameterMetaData());
    }

    @Test
    void testStatementLevelSqlMethodsIntercept() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        when(preparedStatement.executeQuery(anyString())).thenReturn(resultSet);
        when(preparedStatement.executeUpdate(anyString())).thenReturn(1);
        when(preparedStatement.executeUpdate(anyString(), anyInt())).thenReturn(1);
        when(preparedStatement.executeUpdate(anyString(), (int[]) any())).thenReturn(1);
        when(preparedStatement.executeUpdate(anyString(), (String[]) any())).thenReturn(1);
        when(preparedStatement.execute(anyString())).thenReturn(true);
        when(preparedStatement.execute(anyString(), anyInt())).thenReturn(true);
        when(preparedStatement.execute(anyString(), (int[]) any())).thenReturn(true);
        when(preparedStatement.execute(anyString(), (String[]) any())).thenReturn(true);
        when(preparedStatement.executeLargeUpdate(anyString())).thenReturn(2L);
        when(preparedStatement.executeLargeUpdate(anyString(), anyInt())).thenReturn(2L);
        when(preparedStatement.executeLargeUpdate(anyString(), (int[]) any())).thenReturn(2L);
        when(preparedStatement.executeLargeUpdate(anyString(), (String[]) any())).thenReturn(2L);

        String sql = "select name from t_user";

        assertTrue(proxy.executeQuery(sql) instanceof ResultSetProxy);
        assertEquals(1, proxy.executeUpdate(sql));
        assertTrue(proxy.execute(sql));
        proxy.addBatch(sql);
        assertEquals(2L, proxy.executeLargeUpdate(sql));
        assertEquals(1, proxy.executeUpdate(sql, java.sql.Statement.RETURN_GENERATED_KEYS));
        assertEquals(1, proxy.executeUpdate(sql, new int[]{1}));
        assertEquals(1, proxy.executeUpdate(sql, new String[]{"id"}));
        assertTrue(proxy.execute(sql, java.sql.Statement.RETURN_GENERATED_KEYS));
        assertTrue(proxy.execute(sql, new int[]{1}));
        assertTrue(proxy.execute(sql, new String[]{"id"}));
        assertEquals(2L, proxy.executeLargeUpdate(sql, java.sql.Statement.RETURN_GENERATED_KEYS));
        assertEquals(2L, proxy.executeLargeUpdate(sql, new int[]{1}));
        assertEquals(2L, proxy.executeLargeUpdate(sql, new String[]{"id"}));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(preparedStatement).executeQuery(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
        verify(preparedStatement).addBatch(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testResultSetWrapping() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        ResultSet generatedKeys = mockEmptyResultSet();
        when(preparedStatement.getResultSet()).thenReturn(resultSet);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);

        assertTrue(proxy.getResultSet() instanceof ResultSetProxy);
        assertTrue(proxy.getGeneratedKeys() instanceof ResultSetProxy);
    }

    @Test
    void testGetConnectionReturnsConnectionProxy() throws SQLException {
        Connection connection = mock(Connection.class);
        when(preparedStatement.getConnection()).thenReturn(connection);
        assertTrue(proxy.getConnection() instanceof ConnectionProxy);
    }

    @Test
    void testAttributeDelegates() throws SQLException {
        when(preparedStatement.getMaxFieldSize()).thenReturn(1);
        when(preparedStatement.getMaxRows()).thenReturn(2);
        when(preparedStatement.getQueryTimeout()).thenReturn(3);
        SQLWarning warning = mock(SQLWarning.class);
        when(preparedStatement.getWarnings()).thenReturn(warning);
        when(preparedStatement.getUpdateCount()).thenReturn(4);
        when(preparedStatement.getMoreResults()).thenReturn(true);
        when(preparedStatement.getFetchDirection()).thenReturn(ResultSet.FETCH_FORWARD);
        when(preparedStatement.getFetchSize()).thenReturn(5);
        when(preparedStatement.getResultSetConcurrency()).thenReturn(ResultSet.CONCUR_READ_ONLY);
        when(preparedStatement.getResultSetType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        when(preparedStatement.getMoreResults(java.sql.Statement.CLOSE_CURRENT_RESULT)).thenReturn(false);
        when(preparedStatement.getResultSetHoldability()).thenReturn(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        when(preparedStatement.isClosed()).thenReturn(false);
        when(preparedStatement.isPoolable()).thenReturn(true);
        when(preparedStatement.isCloseOnCompletion()).thenReturn(false);
        when(preparedStatement.getLargeUpdateCount()).thenReturn(6L);
        when(preparedStatement.getLargeMaxRows()).thenReturn(7L);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1});
        when(preparedStatement.executeLargeBatch()).thenReturn(new long[]{2L});

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
        assertEquals(false, proxy.getMoreResults(java.sql.Statement.CLOSE_CURRENT_RESULT));
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, proxy.getResultSetHoldability());
        assertEquals(false, proxy.isClosed());
        assertTrue(proxy.isPoolable());
        assertEquals(false, proxy.isCloseOnCompletion());
        assertEquals(6L, proxy.getLargeUpdateCount());
        assertEquals(7L, proxy.getLargeMaxRows());
        assertEquals(1, proxy.executeBatch().length);
        assertEquals(1, proxy.executeLargeBatch().length);

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

        verify(preparedStatement).setMaxFieldSize(10);
        verify(preparedStatement).setMaxRows(20);
        verify(preparedStatement).setEscapeProcessing(true);
        verify(preparedStatement).setQueryTimeout(30);
        verify(preparedStatement).setFetchDirection(ResultSet.FETCH_REVERSE);
        verify(preparedStatement).setFetchSize(40);
        verify(preparedStatement).setPoolable(false);
        verify(preparedStatement).setLargeMaxRows(50L);
        verify(preparedStatement).clearWarnings();
        verify(preparedStatement).clearBatch();
        verify(preparedStatement).closeOnCompletion();
        verify(preparedStatement).close();
        verify(preparedStatement).cancel();
        verify(preparedStatement).setCursorName("cursor");
    }

    @Test
    void testEnquoteAndWrapperDelegates() throws SQLException {
        when(preparedStatement.enquoteLiteral("v")).thenReturn("'v'");
        when(preparedStatement.enquoteIdentifier("id", true)).thenReturn("\"id\"");
        when(preparedStatement.isSimpleIdentifier("id")).thenReturn(true);
        when(preparedStatement.enquoteNCharLiteral("v")).thenReturn("N'v'");
        when(preparedStatement.unwrap(String.class)).thenReturn("unwrapped");
        when(preparedStatement.isWrapperFor(String.class)).thenReturn(true);

        assertEquals("'v'", proxy.enquoteLiteral("v"));
        assertEquals("\"id\"", proxy.enquoteIdentifier("id", true));
        assertTrue(proxy.isSimpleIdentifier("id"));
        assertEquals("N'v'", proxy.enquoteNCharLiteral("v"));
        assertEquals("unwrapped", proxy.unwrap(String.class));
        assertTrue(proxy.isWrapperFor(String.class));
    }

    /**
     * JDK 8 兼容: 构造真实 URL 代替 mock(URL.class)(final 类在 Mockito 4 默认不可 mock)
     */
    private static URL toUrl() {
        try {
            return new URL("http://mock.local/");
        } catch (java.net.MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

}
