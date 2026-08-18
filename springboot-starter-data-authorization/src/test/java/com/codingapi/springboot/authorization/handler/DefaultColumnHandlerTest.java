package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.filter.DefaultDataAuthorizationFilter;
import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;
import com.codingapi.springboot.authorization.interceptor.SQLRunningContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * DefaultColumnHandler 单元测试
 * 验证默认列处理器将请求委托给 DataAuthorizationContext.columnAuthorization：
 * 未拦截时原值返回；拦截且过滤器支持时返回加工后的值
 */
class DefaultColumnHandlerTest {

    private DefaultColumnHandler handler;

    @BeforeEach
    void setUp() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        handler = new DefaultColumnHandler();
    }

    @AfterEach
    void tearDown() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
    }

    @Test
    void testUnInterceptStateReturnsOriginalValues() throws Exception {
        SQLExecuteState state = SQLExecuteState.unIntercept("select 1");
        InputStream inputStream = mock(InputStream.class);
        Reader reader = mock(Reader.class);
        Ref ref = mock(Ref.class);
        Blob blob = mock(Blob.class);
        Clob clob = mock(Clob.class);
        Array array = mock(Array.class);
        URL url = toUrl();
        NClob nClob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        RowId rowId = mock(RowId.class);
        Date date = new Date(1000L);
        Time time = new Time(1000L);
        Timestamp timestamp = new Timestamp(1000L);
        byte[] bytes = new byte[]{1};

        assertEquals("v", handler.getString(state, 1, "t", "c", "v"));
        assertEquals(true, handler.getBoolean(state, 1, "t", "c", true));
        assertEquals((byte) 1, handler.getByte(state, 1, "t", "c", (byte) 1));
        assertEquals((short) 1, handler.getShort(state, 1, "t", "c", (short) 1));
        assertEquals(1, handler.getInt(state, 1, "t", "c", 1));
        assertEquals(1L, handler.getLong(state, 1, "t", "c", 1L));
        assertEquals(1.0f, handler.getFloat(state, 1, "t", "c", 1.0f));
        assertEquals(1.0d, handler.getDouble(state, 1, "t", "c", 1.0d));
        assertEquals(BigDecimal.ONE, handler.getBigDecimal(state, 1, "t", "c", BigDecimal.ONE));
        assertSame(bytes, handler.getBytes(state, 1, "t", "c", bytes));
        assertSame(date, handler.getDate(state, 1, "t", "c", date));
        assertSame(time, handler.getTime(state, 1, "t", "c", time));
        assertSame(timestamp, handler.getTimestamp(state, 1, "t", "c", timestamp));
        assertSame(inputStream, handler.getAsciiStream(state, 1, "t", "c", inputStream));
        assertSame(inputStream, handler.getUnicodeStream(state, 1, "t", "c", inputStream));
        assertSame(inputStream, handler.getBinaryStream(state, 1, "t", "c", inputStream));
        assertEquals("obj", handler.getObject(state, 1, "t", "c", "obj"));
        assertSame(reader, handler.getCharacterStream(state, 1, "t", "c", reader));
        assertSame(ref, handler.getRef(state, 1, "t", "c", ref));
        assertSame(blob, handler.getBlob(state, 1, "t", "c", blob));
        assertSame(clob, handler.getClob(state, 1, "t", "c", clob));
        assertSame(array, handler.getArray(state, 1, "t", "c", array));
        assertSame(url, handler.getURL(state, 1, "t", "c", url));
        assertSame(nClob, handler.getNClob(state, 1, "t", "c", nClob));
        assertSame(sqlxml, handler.getSQLXML(state, 1, "t", "c", sqlxml));
        assertEquals("nv", handler.getNString(state, 1, "t", "c", "nv"));
        assertSame(reader, handler.getNCharacterStream(state, 1, "t", "c", reader));
        assertSame(rowId, handler.getRowId(state, 1, "t", "c", rowId));
        assertEquals("typed", handler.getObject(state, 1, "t", "c", "typed", String.class));
    }

    @Test
    void testInterceptStateAppliesColumnAuthorization() throws Exception {
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
                return "t_user".equalsIgnoreCase(tableName);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                if (value instanceof String) {
                    return (T) ("MASKED-" + value);
                }
                if (value instanceof Integer) {
                    return (T) Integer.valueOf(-1);
                }
                return value;
            }
        });

        SQLExecuteState state = SQLRunningContext.getInstance().intercept("select name from t_user");
        assertTrue(state.hasIntercept());

        assertEquals("MASKED-raw", handler.getString(state, 1, "t_user", "name", "raw"));
        assertEquals(Integer.valueOf(-1), Integer.valueOf(handler.getInt(state, 1, "t_user", "id", 1)));
        assertEquals("MASKED-nv", handler.getNString(state, 1, "t_user", "name", "nv"));
        assertEquals("MASKED-obj", handler.getObject(state, 1, "t_user", "name", "obj"));
        assertEquals("MASKED-typed", handler.getObject(state, 1, "t_user", "name", "typed", String.class));
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
