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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ParameterMetaData;
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
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
 * CallableStatementProxy 单元测试
 * 验证方法委托以及 SQL 拦截行为
 */
class CallableStatementProxyTest {

    private CallableStatement callableStatement;
    private CallableStatementProxy proxy;

    @BeforeEach
    void setUp() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        ResultSetProxyTest.registerTestFilter();

        callableStatement = mock(CallableStatement.class);
        proxy = new CallableStatementProxy(callableStatement, SQLExecuteState.unIntercept("select 1"));
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
    void testRegisterOutParameterDelegates() throws SQLException {
        SQLType sqlType = JDBCType.VARCHAR;

        proxy.registerOutParameter(1, Types.VARCHAR);
        proxy.registerOutParameter(1, Types.VARCHAR, 2);
        proxy.registerOutParameter(1, Types.VARCHAR, "VARCHAR");
        proxy.registerOutParameter("p", Types.VARCHAR);
        proxy.registerOutParameter("p", Types.VARCHAR, 2);
        proxy.registerOutParameter("p", Types.VARCHAR, "VARCHAR");
        proxy.registerOutParameter(1, sqlType);
        proxy.registerOutParameter(1, sqlType, 2);
        proxy.registerOutParameter(1, sqlType, "VARCHAR");
        proxy.registerOutParameter("p", sqlType);
        proxy.registerOutParameter("p", sqlType, 2);
        proxy.registerOutParameter("p", sqlType, "VARCHAR");

        verify(callableStatement).registerOutParameter(1, Types.VARCHAR);
        verify(callableStatement).registerOutParameter(1, Types.VARCHAR, 2);
        verify(callableStatement).registerOutParameter(1, Types.VARCHAR, "VARCHAR");
        verify(callableStatement).registerOutParameter("p", Types.VARCHAR);
        verify(callableStatement).registerOutParameter("p", Types.VARCHAR, 2);
        verify(callableStatement).registerOutParameter("p", Types.VARCHAR, "VARCHAR");
        verify(callableStatement).registerOutParameter(1, sqlType);
        verify(callableStatement).registerOutParameter(1, sqlType, 2);
        verify(callableStatement).registerOutParameter(1, sqlType, "VARCHAR");
        verify(callableStatement).registerOutParameter("p", sqlType);
        verify(callableStatement).registerOutParameter("p", sqlType, 2);
        verify(callableStatement).registerOutParameter("p", sqlType, "VARCHAR");
    }

    @Test
    void testGettersByIndexDelegate() throws SQLException {
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
        Reader reader = new StringReader("x");
        Map<String, Class<?>> map = new HashMap<>();

        when(callableStatement.wasNull()).thenReturn(true);
        when(callableStatement.getString(1)).thenReturn("v");
        when(callableStatement.getBoolean(1)).thenReturn(true);
        when(callableStatement.getByte(1)).thenReturn((byte) 1);
        when(callableStatement.getShort(1)).thenReturn((short) 2);
        when(callableStatement.getInt(1)).thenReturn(3);
        when(callableStatement.getLong(1)).thenReturn(4L);
        when(callableStatement.getFloat(1)).thenReturn(5.0f);
        when(callableStatement.getDouble(1)).thenReturn(6.0d);
        when(callableStatement.getBigDecimal(1, 2)).thenReturn(BigDecimal.ONE);
        when(callableStatement.getBigDecimal(1)).thenReturn(BigDecimal.TEN);
        when(callableStatement.getBytes(1)).thenReturn(new byte[]{1});
        when(callableStatement.getDate(1)).thenReturn(date);
        when(callableStatement.getTime(1)).thenReturn(time);
        when(callableStatement.getTimestamp(1)).thenReturn(timestamp);
        when(callableStatement.getObject(1)).thenReturn("obj");
        when(callableStatement.getObject(1, map)).thenReturn("mapObj");
        when(callableStatement.getRef(1)).thenReturn(ref);
        when(callableStatement.getBlob(1)).thenReturn(blob);
        when(callableStatement.getClob(1)).thenReturn(clob);
        when(callableStatement.getArray(1)).thenReturn(array);
        when(callableStatement.getDate(1, calendar)).thenReturn(date);
        when(callableStatement.getTime(1, calendar)).thenReturn(time);
        when(callableStatement.getTimestamp(1, calendar)).thenReturn(timestamp);
        when(callableStatement.getURL(1)).thenReturn(url);
        when(callableStatement.getRowId(1)).thenReturn(rowId);
        when(callableStatement.getNClob(1)).thenReturn(nClob);
        when(callableStatement.getSQLXML(1)).thenReturn(sqlxml);
        when(callableStatement.getNString(1)).thenReturn("ns");
        when(callableStatement.getNCharacterStream(1)).thenReturn(reader);
        when(callableStatement.getCharacterStream(1)).thenReturn(reader);
        when(callableStatement.getObject(1, String.class)).thenReturn("typed");

        assertTrue(proxy.wasNull());
        assertEquals("v", proxy.getString(1));
        assertTrue(proxy.getBoolean(1));
        assertEquals((byte) 1, proxy.getByte(1));
        assertEquals((short) 2, proxy.getShort(1));
        assertEquals(3, proxy.getInt(1));
        assertEquals(4L, proxy.getLong(1));
        assertEquals(5.0f, proxy.getFloat(1));
        assertEquals(6.0d, proxy.getDouble(1));
        assertEquals(BigDecimal.ONE, proxy.getBigDecimal(1, 2));
        assertEquals(BigDecimal.TEN, proxy.getBigDecimal(1));
        assertEquals(1, proxy.getBytes(1).length);
        assertSame(date, proxy.getDate(1));
        assertSame(time, proxy.getTime(1));
        assertSame(timestamp, proxy.getTimestamp(1));
        assertEquals("obj", proxy.getObject(1));
        assertEquals("mapObj", proxy.getObject(1, map));
        assertSame(ref, proxy.getRef(1));
        assertSame(blob, proxy.getBlob(1));
        assertSame(clob, proxy.getClob(1));
        assertSame(array, proxy.getArray(1));
        assertSame(date, proxy.getDate(1, calendar));
        assertSame(time, proxy.getTime(1, calendar));
        assertSame(timestamp, proxy.getTimestamp(1, calendar));
        assertSame(url, proxy.getURL(1));
        assertSame(rowId, proxy.getRowId(1));
        assertSame(nClob, proxy.getNClob(1));
        assertSame(sqlxml, proxy.getSQLXML(1));
        assertEquals("ns", proxy.getNString(1));
        assertSame(reader, proxy.getNCharacterStream(1));
        assertSame(reader, proxy.getCharacterStream(1));
        assertEquals("typed", proxy.getObject(1, String.class));
    }

    @Test
    void testGettersByNameDelegate() throws SQLException {
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
        Reader reader = new StringReader("x");
        Map<String, Class<?>> map = new HashMap<>();

        when(callableStatement.getString("p")).thenReturn("v");
        when(callableStatement.getBoolean("p")).thenReturn(true);
        when(callableStatement.getByte("p")).thenReturn((byte) 1);
        when(callableStatement.getShort("p")).thenReturn((short) 2);
        when(callableStatement.getInt("p")).thenReturn(3);
        when(callableStatement.getLong("p")).thenReturn(4L);
        when(callableStatement.getFloat("p")).thenReturn(5.0f);
        when(callableStatement.getDouble("p")).thenReturn(6.0d);
        when(callableStatement.getBytes("p")).thenReturn(new byte[]{1});
        when(callableStatement.getDate("p")).thenReturn(date);
        when(callableStatement.getTime("p")).thenReturn(time);
        when(callableStatement.getTimestamp("p")).thenReturn(timestamp);
        when(callableStatement.getObject("p")).thenReturn("obj");
        when(callableStatement.getBigDecimal("p")).thenReturn(BigDecimal.TEN);
        when(callableStatement.getObject("p", map)).thenReturn("mapObj");
        when(callableStatement.getRef("p")).thenReturn(ref);
        when(callableStatement.getBlob("p")).thenReturn(blob);
        when(callableStatement.getClob("p")).thenReturn(clob);
        when(callableStatement.getArray("p")).thenReturn(array);
        when(callableStatement.getDate("p", calendar)).thenReturn(date);
        when(callableStatement.getTime("p", calendar)).thenReturn(time);
        when(callableStatement.getTimestamp("p", calendar)).thenReturn(timestamp);
        when(callableStatement.getURL("p")).thenReturn(url);
        when(callableStatement.getRowId("p")).thenReturn(rowId);
        when(callableStatement.getNClob("p")).thenReturn(nClob);
        when(callableStatement.getSQLXML("p")).thenReturn(sqlxml);
        when(callableStatement.getNString("p")).thenReturn("ns");
        when(callableStatement.getNCharacterStream("p")).thenReturn(reader);
        when(callableStatement.getCharacterStream("p")).thenReturn(reader);
        when(callableStatement.getObject("p", String.class)).thenReturn("typed");

        assertEquals("v", proxy.getString("p"));
        assertTrue(proxy.getBoolean("p"));
        assertEquals((byte) 1, proxy.getByte("p"));
        assertEquals((short) 2, proxy.getShort("p"));
        assertEquals(3, proxy.getInt("p"));
        assertEquals(4L, proxy.getLong("p"));
        assertEquals(5.0f, proxy.getFloat("p"));
        assertEquals(6.0d, proxy.getDouble("p"));
        assertEquals(1, proxy.getBytes("p").length);
        assertSame(date, proxy.getDate("p"));
        assertSame(time, proxy.getTime("p"));
        assertSame(timestamp, proxy.getTimestamp("p"));
        assertEquals("obj", proxy.getObject("p"));
        assertEquals(BigDecimal.TEN, proxy.getBigDecimal("p"));
        assertEquals("mapObj", proxy.getObject("p", map));
        assertSame(ref, proxy.getRef("p"));
        assertSame(blob, proxy.getBlob("p"));
        assertSame(clob, proxy.getClob("p"));
        assertSame(array, proxy.getArray("p"));
        assertSame(date, proxy.getDate("p", calendar));
        assertSame(time, proxy.getTime("p", calendar));
        assertSame(timestamp, proxy.getTimestamp("p", calendar));
        assertSame(url, proxy.getURL("p"));
        assertSame(rowId, proxy.getRowId("p"));
        assertSame(nClob, proxy.getNClob("p"));
        assertSame(sqlxml, proxy.getSQLXML("p"));
        assertEquals("ns", proxy.getNString("p"));
        assertSame(reader, proxy.getNCharacterStream("p"));
        assertSame(reader, proxy.getCharacterStream("p"));
        assertEquals("typed", proxy.getObject("p", String.class));
    }

    @Test
    void testSettersByNameDelegate() throws SQLException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1});
        Reader reader = new StringReader("x");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(1000L);
        Time time = new Time(2000L);
        Timestamp timestamp = new Timestamp(3000L);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        RowId rowId = mock(RowId.class);
        URL url = toUrl();
        SQLType sqlType = JDBCType.VARCHAR;

        proxy.setURL("p", url);
        proxy.setNull("p", Types.VARCHAR);
        proxy.setBoolean("p", true);
        proxy.setByte("p", (byte) 1);
        proxy.setShort("p", (short) 1);
        proxy.setInt("p", 1);
        proxy.setLong("p", 1L);
        proxy.setFloat("p", 1.0f);
        proxy.setDouble("p", 1.0d);
        proxy.setBigDecimal("p", BigDecimal.ONE);
        proxy.setString("p", "x");
        proxy.setBytes("p", new byte[]{1});
        proxy.setDate("p", date);
        proxy.setTime("p", time);
        proxy.setTimestamp("p", timestamp);
        proxy.setAsciiStream("p", inputStream, 1);
        proxy.setBinaryStream("p", inputStream, 1);
        proxy.setObject("p", "x", Types.VARCHAR, 1);
        proxy.setObject("p", "x", Types.VARCHAR);
        proxy.setObject("p", "x");
        proxy.setCharacterStream("p", reader, 1);
        proxy.setDate("p", date, calendar);
        proxy.setTime("p", time, calendar);
        proxy.setTimestamp("p", timestamp, calendar);
        proxy.setNull("p", Types.VARCHAR, "VARCHAR");
        proxy.setRowId("p", rowId);
        proxy.setNString("p", "x");
        proxy.setNCharacterStream("p", reader, 1L);
        proxy.setNClob("p", nClob);
        proxy.setClob("p", reader, 1L);
        proxy.setBlob("p", inputStream, 1L);
        proxy.setNClob("p", reader, 1L);
        proxy.setSQLXML("p", sqlxml);
        proxy.setBlob("p", blob);
        proxy.setClob("p", clob);
        proxy.setAsciiStream("p", inputStream, 1L);
        proxy.setBinaryStream("p", inputStream, 1L);
        proxy.setCharacterStream("p", reader, 1L);
        proxy.setAsciiStream("p", inputStream);
        proxy.setBinaryStream("p", inputStream);
        proxy.setCharacterStream("p", reader);
        proxy.setNCharacterStream("p", reader);
        proxy.setClob("p", reader);
        proxy.setBlob("p", inputStream);
        proxy.setNClob("p", reader);
        proxy.setObject("p", "x", sqlType, 1);
        proxy.setObject("p", "x", sqlType);

        verify(callableStatement).setURL("p", url);
        verify(callableStatement).setNull("p", Types.VARCHAR);
        verify(callableStatement).setBoolean("p", true);
        verify(callableStatement).setByte("p", (byte) 1);
        verify(callableStatement).setShort("p", (short) 1);
        verify(callableStatement).setInt("p", 1);
        verify(callableStatement).setLong("p", 1L);
        verify(callableStatement).setFloat("p", 1.0f);
        verify(callableStatement).setDouble("p", 1.0d);
        verify(callableStatement).setBigDecimal("p", BigDecimal.ONE);
        verify(callableStatement).setString("p", "x");
        verify(callableStatement).setDate("p", date);
        verify(callableStatement).setTime("p", time);
        verify(callableStatement).setTimestamp("p", timestamp);
        verify(callableStatement).setAsciiStream("p", inputStream, 1);
        verify(callableStatement).setBinaryStream("p", inputStream, 1);
        verify(callableStatement).setObject("p", "x", Types.VARCHAR, 1);
        verify(callableStatement).setObject("p", "x", Types.VARCHAR);
        verify(callableStatement).setObject("p", "x");
        verify(callableStatement).setCharacterStream("p", reader, 1);
        verify(callableStatement).setDate("p", date, calendar);
        verify(callableStatement).setTime("p", time, calendar);
        verify(callableStatement).setTimestamp("p", timestamp, calendar);
        verify(callableStatement).setNull("p", Types.VARCHAR, "VARCHAR");
        verify(callableStatement).setRowId("p", rowId);
        verify(callableStatement).setNString("p", "x");
        verify(callableStatement).setNCharacterStream("p", reader, 1L);
        verify(callableStatement).setNClob("p", nClob);
        verify(callableStatement).setClob("p", reader, 1L);
        verify(callableStatement).setBlob("p", inputStream, 1L);
        verify(callableStatement).setNClob("p", reader, 1L);
        verify(callableStatement).setSQLXML("p", sqlxml);
        verify(callableStatement).setBlob("p", blob);
        verify(callableStatement).setClob("p", clob);
        verify(callableStatement).setAsciiStream("p", inputStream, 1L);
        verify(callableStatement).setBinaryStream("p", inputStream, 1L);
        verify(callableStatement).setCharacterStream("p", reader, 1L);
        verify(callableStatement).setAsciiStream("p", inputStream);
        verify(callableStatement).setBinaryStream("p", inputStream);
        verify(callableStatement).setCharacterStream("p", reader);
        verify(callableStatement).setNCharacterStream("p", reader);
        verify(callableStatement).setClob("p", reader);
        verify(callableStatement).setBlob("p", inputStream);
        verify(callableStatement).setNClob("p", reader);
        verify(callableStatement).setObject("p", "x", sqlType, 1);
        verify(callableStatement).setObject("p", "x", sqlType);
    }

    @Test
    void testPreparedStatementLevelSettersDelegate() throws SQLException {
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
        SQLType sqlType = JDBCType.VARCHAR;

        proxy.setNull(1, Types.VARCHAR);
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
        proxy.setObject(1, "x", Types.VARCHAR);
        proxy.setObject(1, "x");
        proxy.setCharacterStream(1, reader, 1);
        proxy.setRef(1, ref);
        proxy.setBlob(1, blob);
        proxy.setClob(1, clob);
        proxy.setArray(1, array);
        proxy.setDate(1, date, calendar);
        proxy.setTime(1, time, calendar);
        proxy.setTimestamp(1, timestamp, calendar);
        proxy.setNull(1, Types.VARCHAR, "VARCHAR");
        proxy.setURL(1, url);
        proxy.setRowId(1, rowId);
        proxy.setNString(1, "x");
        proxy.setNCharacterStream(1, reader, 1L);
        proxy.setNClob(1, nClob);
        proxy.setClob(1, reader, 1L);
        proxy.setBlob(1, inputStream, 1L);
        proxy.setNClob(1, reader, 1L);
        proxy.setSQLXML(1, sqlxml);
        proxy.setObject(1, "x", Types.VARCHAR, 1);
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
        proxy.setObject(1, "x", sqlType, 1);
        proxy.setObject(1, "x", sqlType);
        proxy.addBatch();

        verify(callableStatement).setNull(1, Types.VARCHAR);
        verify(callableStatement).setBoolean(1, true);
        verify(callableStatement).setInt(1, 1);
        verify(callableStatement).setString(1, "x");
        verify(callableStatement).setUnicodeStream(1, inputStream, 1);
        verify(callableStatement).clearParameters();
        verify(callableStatement).setRef(1, ref);
        verify(callableStatement).setBlob(1, blob);
        verify(callableStatement).setClob(1, clob);
        verify(callableStatement).setArray(1, array);
        verify(callableStatement).setSQLXML(1, sqlxml);
        verify(callableStatement).setObject(1, "x", sqlType, 1);
        verify(callableStatement).setObject(1, "x", sqlType);
        verify(callableStatement).addBatch();
    }

    @Test
    void testExecuteMethods() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        when(callableStatement.executeQuery()).thenReturn(resultSet);
        when(callableStatement.executeUpdate()).thenReturn(1);
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.executeLargeUpdate()).thenReturn(2L);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        ParameterMetaData parameterMetaData = mock(ParameterMetaData.class);
        when(callableStatement.getMetaData()).thenReturn(metaData);
        when(callableStatement.getParameterMetaData()).thenReturn(parameterMetaData);
        ResultSet generatedKeys = mockEmptyResultSet();
        when(callableStatement.getGeneratedKeys()).thenReturn(generatedKeys);

        assertTrue(proxy.executeQuery() instanceof ResultSetProxy);
        assertEquals(1, proxy.executeUpdate());
        assertTrue(proxy.execute());
        assertEquals(2L, proxy.executeLargeUpdate());
        assertSame(metaData, proxy.getMetaData());
        assertSame(parameterMetaData, proxy.getParameterMetaData());
        assertSame(generatedKeys, proxy.getGeneratedKeys());
    }

    @Test
    void testStatementLevelSqlMethodsIntercept() throws SQLException {
        ResultSet resultSet = mockEmptyResultSet();
        when(callableStatement.executeQuery(anyString())).thenReturn(resultSet);
        when(callableStatement.executeUpdate(anyString())).thenReturn(1);
        when(callableStatement.executeUpdate(anyString(), anyInt())).thenReturn(1);
        when(callableStatement.executeUpdate(anyString(), (int[]) any())).thenReturn(1);
        when(callableStatement.executeUpdate(anyString(), (String[]) any())).thenReturn(1);
        when(callableStatement.execute(anyString())).thenReturn(true);
        when(callableStatement.execute(anyString(), anyInt())).thenReturn(true);
        when(callableStatement.execute(anyString(), (int[]) any())).thenReturn(true);
        when(callableStatement.execute(anyString(), (String[]) any())).thenReturn(true);
        when(callableStatement.executeLargeUpdate(anyString())).thenReturn(2L);
        when(callableStatement.executeLargeUpdate(anyString(), anyInt())).thenReturn(2L);
        when(callableStatement.executeLargeUpdate(anyString(), (int[]) any())).thenReturn(2L);
        when(callableStatement.executeLargeUpdate(anyString(), (String[]) any())).thenReturn(2L);
        ResultSet rawResultSet = mockEmptyResultSet();
        when(callableStatement.getResultSet()).thenReturn(rawResultSet);
        Connection connection = mock(Connection.class);
        when(callableStatement.getConnection()).thenReturn(connection);

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
        assertTrue(proxy.getResultSet() instanceof ResultSetProxy);
        assertTrue(proxy.getConnection() instanceof ConnectionProxy);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(callableStatement).executeQuery(captor.capture());
        assertTrue(captor.getValue().contains("id > 100"));
    }

    @Test
    void testAttributeDelegates() throws SQLException {
        when(callableStatement.getMaxFieldSize()).thenReturn(1);
        when(callableStatement.getMaxRows()).thenReturn(2);
        when(callableStatement.getQueryTimeout()).thenReturn(3);
        SQLWarning warning = mock(SQLWarning.class);
        when(callableStatement.getWarnings()).thenReturn(warning);
        when(callableStatement.getUpdateCount()).thenReturn(4);
        when(callableStatement.getMoreResults()).thenReturn(true);
        when(callableStatement.getFetchDirection()).thenReturn(ResultSet.FETCH_FORWARD);
        when(callableStatement.getFetchSize()).thenReturn(5);
        when(callableStatement.getResultSetConcurrency()).thenReturn(ResultSet.CONCUR_READ_ONLY);
        when(callableStatement.getResultSetType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        when(callableStatement.getMoreResults(java.sql.Statement.CLOSE_CURRENT_RESULT)).thenReturn(false);
        when(callableStatement.getResultSetHoldability()).thenReturn(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        when(callableStatement.isClosed()).thenReturn(false);
        when(callableStatement.isPoolable()).thenReturn(true);
        when(callableStatement.isCloseOnCompletion()).thenReturn(false);
        when(callableStatement.getLargeUpdateCount()).thenReturn(6L);
        when(callableStatement.getLargeMaxRows()).thenReturn(7L);
        when(callableStatement.executeBatch()).thenReturn(new int[]{1});
        when(callableStatement.executeLargeBatch()).thenReturn(new long[]{2L});

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

        verify(callableStatement).setMaxFieldSize(10);
        verify(callableStatement).setMaxRows(20);
        verify(callableStatement).setEscapeProcessing(true);
        verify(callableStatement).setQueryTimeout(30);
        verify(callableStatement).setFetchDirection(ResultSet.FETCH_REVERSE);
        verify(callableStatement).setFetchSize(40);
        verify(callableStatement).setPoolable(false);
        verify(callableStatement).setLargeMaxRows(50L);
        verify(callableStatement).clearWarnings();
        verify(callableStatement).clearBatch();
        verify(callableStatement).closeOnCompletion();
        verify(callableStatement).close();
        verify(callableStatement).cancel();
        verify(callableStatement).setCursorName("cursor");
    }

    @Test
    void testEnquoteAndWrapperDelegates() throws SQLException {
        when(callableStatement.enquoteLiteral("v")).thenReturn("'v'");
        when(callableStatement.enquoteIdentifier("id", true)).thenReturn("\"id\"");
        when(callableStatement.isSimpleIdentifier("id")).thenReturn(true);
        when(callableStatement.enquoteNCharLiteral("v")).thenReturn("N'v'");
        when(callableStatement.unwrap(String.class)).thenReturn("unwrapped");
        when(callableStatement.isWrapperFor(String.class)).thenReturn(true);

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
