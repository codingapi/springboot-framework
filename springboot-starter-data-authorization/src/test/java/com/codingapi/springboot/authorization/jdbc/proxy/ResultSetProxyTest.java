package com.codingapi.springboot.authorization.jdbc.proxy;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.filter.DefaultDataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;
import com.codingapi.springboot.authorization.interceptor.SQLRunningContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ResultSetProxy 单元测试
 * 验证所有方法正确委托到底层 ResultSet，以及列权限拦截逻辑生效
 */
class ResultSetProxyTest {

    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private ResultSetProxy proxy;

    /**
     * 构造一个 t_user 表的行权限过滤器（id > 100）以及 name 列的列权限过滤器
     */
    static void registerTestFilter() {
        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DefaultDataAuthorizationFilter() {
            @Override
            public boolean supportRowAuthorization(String tableName, String tableAlias) {
                return "t_user".equalsIgnoreCase(tableName);
            }

            @Override
            public Condition rowAuthorization(String tableName, String tableAlias) {
                return Condition.formatCondition("%s.id > 100", tableAlias);
            }

            @Override
            public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
                return "t_user".equalsIgnoreCase(tableName) && "name".equalsIgnoreCase(columnName);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                if (value instanceof String) {
                    return (T) ("MASKED-" + value);
                }
                return value;
            }
        });
    }

    @BeforeEach
    void setUp() throws SQLException {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        registerTestFilter();

        resultSet = mock(ResultSet.class);
        metaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("name");
        when(metaData.getTableName(1)).thenReturn("t_user");
        when(metaData.getColumnName(1)).thenReturn("name");
        // 默认使用未拦截状态，列权限不生效，直接透传底层值
        proxy = new ResultSetProxy(resultSet, SQLExecuteState.unIntercept("select 1"));
    }

    @AfterEach
    void tearDown() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
    }

    @Test
    void testNavigationAndCloseDelegates() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(true);
        when(resultSet.isBeforeFirst()).thenReturn(true);
        when(resultSet.isAfterLast()).thenReturn(false);
        when(resultSet.isFirst()).thenReturn(true);
        when(resultSet.isLast()).thenReturn(false);
        when(resultSet.first()).thenReturn(true);
        when(resultSet.last()).thenReturn(true);
        when(resultSet.getRow()).thenReturn(5);
        when(resultSet.absolute(3)).thenReturn(true);
        when(resultSet.relative(2)).thenReturn(true);
        when(resultSet.previous()).thenReturn(false);
        when(resultSet.isClosed()).thenReturn(false);
        when(resultSet.getHoldability()).thenReturn(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        when(resultSet.getType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        when(resultSet.getConcurrency()).thenReturn(ResultSet.CONCUR_READ_ONLY);
        when(resultSet.getFetchDirection()).thenReturn(ResultSet.FETCH_FORWARD);
        when(resultSet.getFetchSize()).thenReturn(10);
        when(resultSet.rowUpdated()).thenReturn(true);
        when(resultSet.rowInserted()).thenReturn(false);
        when(resultSet.rowDeleted()).thenReturn(false);

        assertTrue(proxy.next());
        verify(resultSet).next();
        assertTrue(proxy.wasNull());
        verify(resultSet).wasNull();

        proxy.close();
        verify(resultSet).close();

        assertTrue(proxy.isBeforeFirst());
        assertFalseNoImport(proxy.isAfterLast());
        assertTrue(proxy.isFirst());
        assertFalseNoImport(proxy.isLast());
        assertTrue(proxy.first());
        assertTrue(proxy.last());
        assertEquals(5, proxy.getRow());
        assertTrue(proxy.absolute(3));
        assertTrue(proxy.relative(2));
        assertFalseNoImport(proxy.previous());
        assertFalseNoImport(proxy.isClosed());
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, proxy.getHoldability());
        assertEquals(ResultSet.TYPE_FORWARD_ONLY, proxy.getType());
        assertEquals(ResultSet.CONCUR_READ_ONLY, proxy.getConcurrency());
        assertEquals(ResultSet.FETCH_FORWARD, proxy.getFetchDirection());
        assertEquals(10, proxy.getFetchSize());
        assertTrue(proxy.rowUpdated());
        assertFalseNoImport(proxy.rowInserted());
        assertFalseNoImport(proxy.rowDeleted());

        proxy.beforeFirst();
        verify(resultSet).beforeFirst();
        proxy.afterLast();
        verify(resultSet).afterLast();
        proxy.setFetchDirection(ResultSet.FETCH_REVERSE);
        verify(resultSet).setFetchDirection(ResultSet.FETCH_REVERSE);
        proxy.setFetchSize(20);
        verify(resultSet).setFetchSize(20);
    }

    private static void assertFalseNoImport(boolean value) {
        assertEquals(false, value);
    }

    @Test
    void testGettersByColumnIndexDelegate() throws SQLException {
        when(resultSet.getString(1)).thenReturn("value");
        when(resultSet.getBoolean(1)).thenReturn(true);
        when(resultSet.getByte(1)).thenReturn((byte) 1);
        when(resultSet.getShort(1)).thenReturn((short) 2);
        when(resultSet.getInt(1)).thenReturn(3);
        when(resultSet.getLong(1)).thenReturn(4L);
        when(resultSet.getFloat(1)).thenReturn(5.0f);
        when(resultSet.getDouble(1)).thenReturn(6.0d);
        when(resultSet.getBigDecimal(1)).thenReturn(BigDecimal.ONE);
        when(resultSet.getBigDecimal(1, 2)).thenReturn(BigDecimal.TEN);
        when(resultSet.getBytes(1)).thenReturn(new byte[]{1, 2});
        Date date = new Date(1000L);
        when(resultSet.getDate(1)).thenReturn(date);
        Time time = new Time(2000L);
        when(resultSet.getTime(1)).thenReturn(time);
        Timestamp timestamp = new Timestamp(3000L);
        when(resultSet.getTimestamp(1)).thenReturn(timestamp);
        InputStream asciiStream = new ByteArrayInputStream(new byte[]{1});
        when(resultSet.getAsciiStream(1)).thenReturn(asciiStream);
        InputStream unicodeStream = new ByteArrayInputStream(new byte[]{2});
        when(resultSet.getUnicodeStream(1)).thenReturn(unicodeStream);
        InputStream binaryStream = new ByteArrayInputStream(new byte[]{3});
        when(resultSet.getBinaryStream(1)).thenReturn(binaryStream);
        when(resultSet.getObject(1)).thenReturn("object");
        Reader characterReader = new StringReader("char");
        when(resultSet.getCharacterStream(1)).thenReturn(characterReader);

        // 未拦截状态下，所有值原样返回
        assertEquals("value", proxy.getString(1));
        assertEquals(true, proxy.getBoolean(1));
        assertEquals((byte) 1, proxy.getByte(1));
        assertEquals((short) 2, proxy.getShort(1));
        assertEquals(3, proxy.getInt(1));
        assertEquals(4L, proxy.getLong(1));
        assertEquals(5.0f, proxy.getFloat(1));
        assertEquals(6.0d, proxy.getDouble(1));
        assertEquals(BigDecimal.ONE, proxy.getBigDecimal(1));
        assertEquals(BigDecimal.TEN, proxy.getBigDecimal(1, 2));
        assertSame(date, proxy.getDate(1));
        assertSame(time, proxy.getTime(1));
        assertSame(timestamp, proxy.getTimestamp(1));
        assertSame(asciiStream, proxy.getAsciiStream(1));
        assertSame(unicodeStream, proxy.getUnicodeStream(1));
        assertSame(binaryStream, proxy.getBinaryStream(1));
        assertEquals("object", proxy.getObject(1));
        assertSame(characterReader, proxy.getCharacterStream(1));

        verify(resultSet).getString(1);
        verify(resultSet).getInt(1);
    }

    @Test
    void testGettersByColumnLabelDelegate() throws SQLException {
        when(resultSet.getString(1)).thenReturn("value");
        when(resultSet.getBoolean(1)).thenReturn(true);
        when(resultSet.getByte(1)).thenReturn((byte) 1);
        when(resultSet.getShort(1)).thenReturn((short) 2);
        when(resultSet.getInt(1)).thenReturn(3);
        when(resultSet.getLong(1)).thenReturn(4L);
        when(resultSet.getFloat(1)).thenReturn(5.0f);
        when(resultSet.getDouble(1)).thenReturn(6.0d);
        when(resultSet.getBigDecimal(1)).thenReturn(BigDecimal.ONE);
        when(resultSet.getBigDecimal(1, 2)).thenReturn(BigDecimal.TEN);
        when(resultSet.getBytes(1)).thenReturn(new byte[]{1});
        Date date = new Date(1000L);
        when(resultSet.getDate(1)).thenReturn(date);
        Time time = new Time(2000L);
        when(resultSet.getTime(1)).thenReturn(time);
        Timestamp timestamp = new Timestamp(3000L);
        when(resultSet.getTimestamp(1)).thenReturn(timestamp);
        InputStream asciiStream = new ByteArrayInputStream(new byte[]{1});
        when(resultSet.getAsciiStream(1)).thenReturn(asciiStream);
        InputStream unicodeStream = new ByteArrayInputStream(new byte[]{2});
        when(resultSet.getUnicodeStream(1)).thenReturn(unicodeStream);
        InputStream binaryStream = new ByteArrayInputStream(new byte[]{3});
        when(resultSet.getBinaryStream(1)).thenReturn(binaryStream);
        when(resultSet.getObject(1)).thenReturn("object");
        Reader characterReader = new StringReader("char");
        when(resultSet.getCharacterStream(1)).thenReturn(characterReader);

        // 通过列标签访问，内部根据 columnLabelMap 转换为索引 1
        assertEquals("value", proxy.getString("name"));
        assertEquals("value", proxy.getString("NAME"));
        assertEquals(true, proxy.getBoolean("name"));
        assertEquals((byte) 1, proxy.getByte("name"));
        assertEquals((short) 2, proxy.getShort("name"));
        assertEquals(3, proxy.getInt("name"));
        assertEquals(4L, proxy.getLong("name"));
        assertEquals(5.0f, proxy.getFloat("name"));
        assertEquals(6.0d, proxy.getDouble("name"));
        assertEquals(BigDecimal.ONE, proxy.getBigDecimal("name"));
        assertEquals(BigDecimal.TEN, proxy.getBigDecimal("name", 2));
        assertSame(date, proxy.getDate("name"));
        assertSame(time, proxy.getTime("name"));
        assertSame(timestamp, proxy.getTimestamp("name"));
        assertSame(asciiStream, proxy.getAsciiStream("name"));
        assertSame(unicodeStream, proxy.getUnicodeStream("name"));
        assertSame(binaryStream, proxy.getBinaryStream("name"));
        assertEquals("object", proxy.getObject("name"));
        assertSame(characterReader, proxy.getCharacterStream("name"));
    }

    @Test
    void testColumnAuthorizationWithInterceptState() throws SQLException {
        // 使用真实拦截流程构造拦截状态：select name from t_user 会被注入 t_user.id > 100
        SQLExecuteState interceptState = SQLRunningContext.getInstance().intercept("select name from t_user");
        assertTrue(interceptState.hasIntercept());
        ResultSetProxy interceptProxy = new ResultSetProxy(resultSet, interceptState);

        when(resultSet.getString(1)).thenReturn("raw");
        // 列权限过滤器生效，t_user.name 的值被加工
        assertEquals("MASKED-raw", interceptProxy.getString(1));
        assertEquals("MASKED-raw", interceptProxy.getString("name"));

        when(resultSet.getNString(1)).thenReturn("nraw");
        assertEquals("MASKED-nraw", interceptProxy.getNString(1));
        assertEquals("MASKED-nraw", interceptProxy.getNString("name"));

        when(resultSet.getObject(1)).thenReturn("oraw");
        assertEquals("MASKED-oraw", interceptProxy.getObject(1));
        assertEquals("MASKED-oraw", interceptProxy.getObject("name"));
    }

    @Test
    void testSimpleDelegates() throws SQLException {
        SQLWarning warning = mock(SQLWarning.class);
        when(resultSet.getWarnings()).thenReturn(warning);
        when(resultSet.getCursorName()).thenReturn("cursor");
        when(resultSet.findColumn("name")).thenReturn(1);
        Statement statement = mock(Statement.class);
        when(resultSet.getStatement()).thenReturn(statement);

        assertSame(warning, proxy.getWarnings());
        proxy.clearWarnings();
        verify(resultSet).clearWarnings();
        assertEquals("cursor", proxy.getCursorName());
        assertSame(metaData, proxy.getMetaData());
        assertEquals(1, proxy.findColumn("name"));
        assertSame(statement, proxy.getStatement());
    }

    @Test
    void testObjectAndLobGettersByIndex() throws SQLException {
        Ref ref = mock(Ref.class);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        Array array = mock(Array.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        RowId rowId = mock(RowId.class);
        URL url = mock(URL.class);
        Date date = new Date(1000L);

        when(resultSet.getRef(1)).thenReturn(ref);
        when(resultSet.getBlob(1)).thenReturn(blob);
        when(resultSet.getClob(1)).thenReturn(clob);
        when(resultSet.getArray(1)).thenReturn(array);
        when(resultSet.getNClob(1)).thenReturn(nClob);
        when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
        when(resultSet.getRowId(1)).thenReturn(rowId);
        when(resultSet.getURL(1)).thenReturn(url);
        when(resultSet.getDate(1)).thenReturn(date);
        when(resultSet.getObject(1)).thenReturn("obj");
        when(resultSet.getObject(1, String.class)).thenReturn("typed");

        Map<String, Class<?>> map = new HashMap<>();
        // 注意：getObject(int, Map) 当前实现内部调用的是 resultSet.getDate(columnIndex)
        // 此处断言的是当前（有缺陷的）行为，详见测试报告
        assertSame(date, proxy.getObject(1, map));
        assertSame(ref, proxy.getRef(1));
        assertSame(blob, proxy.getBlob(1));
        assertSame(clob, proxy.getClob(1));
        assertSame(array, proxy.getArray(1));
        assertSame(nClob, proxy.getNClob(1));
        assertSame(sqlxml, proxy.getSQLXML(1));
        assertSame(rowId, proxy.getRowId(1));
        assertSame(url, proxy.getURL(1));
        assertEquals("typed", proxy.getObject(1, String.class));
    }

    @Test
    void testObjectAndLobGettersByLabel() throws SQLException {
        Ref ref = mock(Ref.class);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        Array array = mock(Array.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        RowId rowId = mock(RowId.class);
        URL url = mock(URL.class);
        Date date = new Date(1000L);

        when(resultSet.getRef(1)).thenReturn(ref);
        when(resultSet.getBlob(1)).thenReturn(blob);
        when(resultSet.getClob(1)).thenReturn(clob);
        when(resultSet.getArray(1)).thenReturn(array);
        when(resultSet.getNClob(1)).thenReturn(nClob);
        when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
        when(resultSet.getRowId(1)).thenReturn(rowId);
        when(resultSet.getURL(1)).thenReturn(url);
        when(resultSet.getDate(1)).thenReturn(date);
        when(resultSet.getObject(1)).thenReturn("obj");
        when(resultSet.getObject(1, String.class)).thenReturn("typed");

        Map<String, Class<?>> map = new HashMap<>();
        // getObject(String, Map) 内部调用的是 getObject(columnIndex)，忽略了 map 参数
        assertEquals("obj", proxy.getObject("name", map));
        assertSame(ref, proxy.getRef("name"));
        assertSame(blob, proxy.getBlob("name"));
        assertSame(clob, proxy.getClob("name"));
        assertSame(array, proxy.getArray("name"));
        assertSame(nClob, proxy.getNClob("name"));
        assertSame(sqlxml, proxy.getSQLXML("name"));
        assertSame(rowId, proxy.getRowId("name"));
        assertSame(url, proxy.getURL("name"));
        assertEquals("typed", proxy.getObject("name", String.class));
    }

    @Test
    void testCalendarGetters() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(1000L);
        Time time = new Time(2000L);
        Timestamp timestamp = new Timestamp(3000L);

        when(resultSet.getDate(1, calendar)).thenReturn(date);
        when(resultSet.getTime(1, calendar)).thenReturn(time);
        when(resultSet.getTimestamp(1, calendar)).thenReturn(timestamp);

        assertSame(date, proxy.getDate(1, calendar));
        assertSame(date, proxy.getDate("name", calendar));
        assertSame(time, proxy.getTime(1, calendar));
        assertSame(time, proxy.getTime("name", calendar));
        assertSame(timestamp, proxy.getTimestamp(1, calendar));
        assertSame(timestamp, proxy.getTimestamp("name", calendar));
    }

    @Test
    void testNCharacterStreamGetters() throws SQLException {
        Reader reader = new StringReader("nchar");
        when(resultSet.getNCharacterStream(1)).thenReturn(reader);
        assertSame(reader, proxy.getNCharacterStream(1));
        assertSame(reader, proxy.getNCharacterStream("name"));
    }

    @Test
    void testUpdateMethodsByIndex() throws SQLException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1});
        Reader reader = new StringReader("x");
        Date date = new Date(1000L);
        Time time = new Time(2000L);
        Timestamp timestamp = new Timestamp(3000L);
        Ref ref = mock(Ref.class);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        Array array = mock(Array.class);
        RowId rowId = mock(RowId.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);

        proxy.updateNull(1);
        proxy.updateBoolean(1, true);
        proxy.updateByte(1, (byte) 1);
        proxy.updateShort(1, (short) 1);
        proxy.updateInt(1, 1);
        proxy.updateLong(1, 1L);
        proxy.updateFloat(1, 1.0f);
        proxy.updateDouble(1, 1.0d);
        proxy.updateBigDecimal(1, BigDecimal.ONE);
        proxy.updateString(1, "x");
        proxy.updateBytes(1, new byte[]{1});
        proxy.updateDate(1, date);
        proxy.updateTime(1, time);
        proxy.updateTimestamp(1, timestamp);
        proxy.updateAsciiStream(1, inputStream, 1);
        proxy.updateBinaryStream(1, inputStream, 1);
        proxy.updateCharacterStream(1, reader, 1);
        proxy.updateObject(1, "x", 1);
        proxy.updateObject(1, "x");
        proxy.updateRef(1, ref);
        proxy.updateBlob(1, blob);
        proxy.updateClob(1, clob);
        proxy.updateArray(1, array);
        proxy.updateRowId(1, rowId);
        proxy.updateNString(1, "x");
        proxy.updateNClob(1, nClob);
        proxy.updateSQLXML(1, sqlxml);
        proxy.updateNCharacterStream(1, reader, 1L);
        proxy.updateAsciiStream(1, inputStream, 1L);
        proxy.updateBinaryStream(1, inputStream, 1L);
        proxy.updateCharacterStream(1, reader, 1L);
        proxy.updateBlob(1, inputStream, 1L);
        proxy.updateClob(1, reader, 1L);
        proxy.updateNClob(1, reader, 1L);
        proxy.updateNCharacterStream(1, reader);
        proxy.updateAsciiStream(1, inputStream);
        proxy.updateBinaryStream(1, inputStream);
        proxy.updateCharacterStream(1, reader);
        proxy.updateBlob(1, inputStream);
        proxy.updateClob(1, reader);
        proxy.updateNClob(1, reader);
        proxy.updateObject(1, "x", java.sql.JDBCType.VARCHAR, 1);
        proxy.updateObject(1, "x", java.sql.JDBCType.VARCHAR);

        verify(resultSet).updateNull(1);
        verify(resultSet).updateBoolean(1, true);
        verify(resultSet).updateByte(1, (byte) 1);
        verify(resultSet).updateShort(1, (short) 1);
        verify(resultSet).updateInt(1, 1);
        verify(resultSet).updateLong(1, 1L);
        verify(resultSet).updateFloat(1, 1.0f);
        verify(resultSet).updateDouble(1, 1.0d);
        verify(resultSet).updateBigDecimal(1, BigDecimal.ONE);
        verify(resultSet).updateString(1, "x");
        verify(resultSet).updateDate(1, date);
        verify(resultSet).updateTime(1, time);
        verify(resultSet).updateTimestamp(1, timestamp);
        verify(resultSet).updateAsciiStream(1, inputStream, 1);
        verify(resultSet).updateBinaryStream(1, inputStream, 1);
        verify(resultSet).updateCharacterStream(1, reader, 1);
        verify(resultSet).updateObject(1, "x", 1);
        verify(resultSet).updateObject(1, "x");
        verify(resultSet).updateRef(1, ref);
        verify(resultSet).updateBlob(1, blob);
        verify(resultSet).updateClob(1, clob);
        verify(resultSet).updateArray(1, array);
        verify(resultSet).updateRowId(1, rowId);
        verify(resultSet).updateNString(1, "x");
        verify(resultSet).updateNClob(1, nClob);
        verify(resultSet).updateSQLXML(1, sqlxml);
        verify(resultSet).updateNCharacterStream(1, reader, 1L);
        verify(resultSet).updateAsciiStream(1, inputStream, 1L);
        verify(resultSet).updateBinaryStream(1, inputStream, 1L);
        verify(resultSet).updateCharacterStream(1, reader, 1L);
        verify(resultSet).updateBlob(1, inputStream, 1L);
        verify(resultSet).updateClob(1, reader, 1L);
        verify(resultSet).updateNClob(1, reader, 1L);
        verify(resultSet).updateNCharacterStream(1, reader);
        verify(resultSet).updateAsciiStream(1, inputStream);
        verify(resultSet).updateBinaryStream(1, inputStream);
        verify(resultSet).updateCharacterStream(1, reader);
        verify(resultSet).updateBlob(1, inputStream);
        verify(resultSet).updateClob(1, reader);
        verify(resultSet).updateNClob(1, reader);
        verify(resultSet).updateObject(1, "x", java.sql.JDBCType.VARCHAR, 1);
        verify(resultSet).updateObject(1, "x", java.sql.JDBCType.VARCHAR);
    }

    @Test
    void testUpdateMethodsByLabel() throws SQLException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1});
        Reader reader = new StringReader("x");
        Date date = new Date(1000L);
        Time time = new Time(2000L);
        Timestamp timestamp = new Timestamp(3000L);
        Ref ref = mock(Ref.class);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        Array array = mock(Array.class);
        RowId rowId = mock(RowId.class);
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);

        proxy.updateNull("name");
        proxy.updateBoolean("name", true);
        proxy.updateByte("name", (byte) 1);
        proxy.updateShort("name", (short) 1);
        proxy.updateInt("name", 1);
        proxy.updateLong("name", 1L);
        proxy.updateFloat("name", 1.0f);
        proxy.updateDouble("name", 1.0d);
        proxy.updateBigDecimal("name", BigDecimal.ONE);
        proxy.updateString("name", "x");
        proxy.updateBytes("name", new byte[]{1});
        proxy.updateDate("name", date);
        proxy.updateTime("name", time);
        proxy.updateTimestamp("name", timestamp);
        proxy.updateAsciiStream("name", inputStream, 1);
        proxy.updateBinaryStream("name", inputStream, 1);
        proxy.updateCharacterStream("name", reader, 1);
        proxy.updateObject("name", "x", 1);
        proxy.updateObject("name", "x");
        proxy.updateRef("name", ref);
        proxy.updateBlob("name", blob);
        proxy.updateClob("name", clob);
        proxy.updateArray("name", array);
        proxy.updateRowId("name", rowId);
        proxy.updateNString("name", "x");
        proxy.updateNClob("name", nClob);
        proxy.updateSQLXML("name", sqlxml);
        proxy.updateNCharacterStream("name", reader, 1L);
        proxy.updateAsciiStream("name", inputStream, 1L);
        proxy.updateBinaryStream("name", inputStream, 1L);
        proxy.updateCharacterStream("name", reader, 1L);
        proxy.updateBlob("name", inputStream, 1L);
        proxy.updateClob("name", reader, 1L);
        proxy.updateNClob("name", reader, 1L);
        proxy.updateNCharacterStream("name", reader);
        proxy.updateAsciiStream("name", inputStream);
        proxy.updateBinaryStream("name", inputStream);
        proxy.updateCharacterStream("name", reader);
        proxy.updateBlob("name", inputStream);
        proxy.updateClob("name", reader);
        proxy.updateNClob("name", reader);
        proxy.updateObject("name", "x", java.sql.JDBCType.VARCHAR, 1);
        proxy.updateObject("name", "x", java.sql.JDBCType.VARCHAR);

        verify(resultSet).updateNull("name");
        verify(resultSet).updateBoolean("name", true);
        verify(resultSet).updateString("name", "x");
        verify(resultSet).updateDate("name", date);
        verify(resultSet).updateTime("name", time);
        verify(resultSet).updateTimestamp("name", timestamp);
        verify(resultSet).updateRef("name", ref);
        verify(resultSet).updateBlob("name", blob);
        verify(resultSet).updateClob("name", clob);
        verify(resultSet).updateArray("name", array);
        verify(resultSet).updateRowId("name", rowId);
        verify(resultSet).updateNString("name", "x");
        verify(resultSet).updateNClob("name", nClob);
        verify(resultSet).updateSQLXML("name", sqlxml);
        verify(resultSet).updateObject("name", "x", java.sql.JDBCType.VARCHAR, 1);
        verify(resultSet).updateObject("name", "x", java.sql.JDBCType.VARCHAR);
    }

    @Test
    void testRowManipulationDelegates() throws SQLException {
        proxy.insertRow();
        proxy.updateRow();
        proxy.deleteRow();
        proxy.refreshRow();
        proxy.cancelRowUpdates();
        proxy.moveToInsertRow();
        proxy.moveToCurrentRow();

        verify(resultSet).insertRow();
        verify(resultSet).updateRow();
        verify(resultSet).deleteRow();
        verify(resultSet).refreshRow();
        verify(resultSet).cancelRowUpdates();
        verify(resultSet).moveToInsertRow();
        verify(resultSet).moveToCurrentRow();
    }

    @Test
    void testWrapperDelegates() throws SQLException {
        when(resultSet.unwrap(String.class)).thenReturn("unwrapped");
        when(resultSet.isWrapperFor(String.class)).thenReturn(true);

        assertEquals("unwrapped", proxy.unwrap(String.class));
        assertTrue(proxy.isWrapperFor(String.class));
    }

    @Test
    void testGetStringWithUnrelatedColumnNotMasked() throws SQLException {
        // 拦截状态下，非受管列（columnName 非 name）不触发列权限，值原样返回
        SQLExecuteState interceptState = SQLRunningContext.getInstance().intercept("select name from t_user");
        when(metaData.getColumnName(1)).thenReturn("other_column");
        when(resultSet.getString(1)).thenReturn("plain");
        ResultSetProxy interceptProxy = new ResultSetProxy(resultSet, interceptState);
        assertEquals("plain", interceptProxy.getString(1));
    }
}
