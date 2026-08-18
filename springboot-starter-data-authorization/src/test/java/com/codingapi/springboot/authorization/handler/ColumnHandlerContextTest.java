package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ColumnHandlerContext 单元测试
 * 验证上下文将各类 get 请求委托给注册的 ColumnHandler
 */
class ColumnHandlerContextTest {

    private ColumnHandler columnHandler;
    private SQLExecuteState state;

    @BeforeEach
    void setUp() {
        columnHandler = mock(ColumnHandler.class);
        ColumnHandlerContext.getInstance().setColumnHandler(columnHandler);
        state = SQLExecuteState.unIntercept("select 1");
    }

    @AfterEach
    void tearDown() {
        // 恢复默认处理器，避免影响其他测试
        ColumnHandlerContext.getInstance().setColumnHandler(new DefaultColumnHandler());
    }

    @Test
    void testDelegatesToColumnHandler() throws Exception {
        when(columnHandler.getString(any(), anyInt(), anyString(), anyString(), anyString())).thenReturn("s");
        when(columnHandler.getShort(any(), anyInt(), anyString(), anyString(), anyShort())).thenReturn((short) 1);
        when(columnHandler.getBoolean(any(), anyInt(), anyString(), anyString(), anyBoolean())).thenReturn(true);
        when(columnHandler.getByte(any(), anyInt(), anyString(), anyString(), anyByte())).thenReturn((byte) 2);
        when(columnHandler.getInt(any(), anyInt(), anyString(), anyString(), anyInt())).thenReturn(3);
        when(columnHandler.getLong(any(), anyInt(), anyString(), anyString(), anyLong())).thenReturn(4L);
        when(columnHandler.getFloat(any(), anyInt(), anyString(), anyString(), anyFloat())).thenReturn(5.0f);
        when(columnHandler.getDouble(any(), anyInt(), anyString(), anyString(), anyDouble())).thenReturn(6.0d);
        when(columnHandler.getBigDecimal(any(), anyInt(), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(BigDecimal.ONE);
        when(columnHandler.getBytes(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(new byte[]{1});
        Timestamp timestamp = new Timestamp(1000L);
        when(columnHandler.getTimestamp(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(timestamp);
        Time time = new Time(1000L);
        when(columnHandler.getTime(any(), anyInt(), anyString(), anyString(), any())).thenReturn(time);
        Date date = new Date(1000L);
        when(columnHandler.getDate(any(), anyInt(), anyString(), anyString(), any())).thenReturn(date);
        InputStream inputStream = mock(InputStream.class);
        when(columnHandler.getAsciiStream(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(inputStream);
        when(columnHandler.getUnicodeStream(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(inputStream);
        when(columnHandler.getBinaryStream(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(inputStream);
        when(columnHandler.getObject(any(), anyInt(), anyString(), anyString(), any())).thenReturn("obj");
        Reader reader = mock(Reader.class);
        when(columnHandler.getCharacterStream(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(reader);
        when(columnHandler.getNCharacterStream(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(reader);
        Ref ref = mock(Ref.class);
        when(columnHandler.getRef(any(), anyInt(), anyString(), anyString(), any())).thenReturn(ref);
        Blob blob = mock(Blob.class);
        when(columnHandler.getBlob(any(), anyInt(), anyString(), anyString(), any())).thenReturn(blob);
        Clob clob = mock(Clob.class);
        when(columnHandler.getClob(any(), anyInt(), anyString(), anyString(), any())).thenReturn(clob);
        Array array = mock(Array.class);
        when(columnHandler.getArray(any(), anyInt(), anyString(), anyString(), any())).thenReturn(array);
        URL url = mock(URL.class);
        when(columnHandler.getURL(any(), anyInt(), anyString(), anyString(), any())).thenReturn(url);
        NClob nClob = mock(NClob.class);
        when(columnHandler.getNClob(any(), anyInt(), anyString(), anyString(), any())).thenReturn(nClob);
        SQLXML sqlxml = mock(SQLXML.class);
        when(columnHandler.getSQLXML(any(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(sqlxml);
        when(columnHandler.getNString(any(), anyInt(), anyString(), anyString(), anyString())).thenReturn("ns");
        RowId rowId = mock(RowId.class);
        when(columnHandler.getRowId(any(), anyInt(), anyString(), anyString(), any())).thenReturn(rowId);
        when(columnHandler.getObject(any(), anyInt(), anyString(), anyString(), any(), eq(String.class)))
                .thenReturn("typed");

        ColumnHandlerContext context = ColumnHandlerContext.getInstance();

        assertEquals("s", context.getString(state, 1, "t", "c", "v"));
        assertEquals((short) 1, context.getShort(state, 1, "t", "c", (short) 0));
        assertEquals(true, context.getBoolean(state, 1, "t", "c", false));
        assertEquals((byte) 2, context.getByte(state, 1, "t", "c", (byte) 0));
        assertEquals(3, context.getInt(state, 1, "t", "c", 0));
        assertEquals(4L, context.getLong(state, 1, "t", "c", 0L));
        assertEquals(5.0f, context.getFloat(state, 1, "t", "c", 0f));
        assertEquals(6.0d, context.getDouble(state, 1, "t", "c", 0d));
        assertEquals(BigDecimal.ONE, context.getBigDecimal(state, 1, "t", "c", BigDecimal.ZERO));
        assertSame(timestamp, context.getTimestamp(state, 1, "t", "c", null));
        assertSame(time, context.getTime(state, 1, "t", "c", null));
        assertSame(date, context.getDate(state, 1, "t", "c", null));
        assertSame(inputStream, context.getAsciiStream(state, 1, "t", "c", null));
        assertSame(inputStream, context.getUnicodeStream(state, 1, "t", "c", null));
        assertSame(inputStream, context.getBinaryStream(state, 1, "t", "c", null));
        assertEquals("obj", context.getObject(state, 1, "t", "c", null));
        assertSame(reader, context.getCharacterStream(state, 1, "t", "c", null));
        assertSame(reader, context.getNCharacterStream(state, 1, "t", "c", null));
        assertSame(ref, context.getRef(state, 1, "t", "c", null));
        assertSame(blob, context.getBlob(state, 1, "t", "c", null));
        assertSame(clob, context.getClob(state, 1, "t", "c", null));
        assertSame(array, context.getArray(state, 1, "t", "c", null));
        assertSame(url, context.getURL(state, 1, "t", "c", null));
        assertSame(nClob, context.getNClob(state, 1, "t", "c", null));
        assertSame(sqlxml, context.getSQLXML(state, 1, "t", "c", null));
        assertEquals("ns", context.getNString(state, 1, "t", "c", "v"));
        assertSame(rowId, context.getRowId(state, 1, "t", "c", null));
        assertEquals("typed", context.getObject(state, 1, "t", "c", "v", String.class));
        assertEquals(1, context.getBytes(state, 1, "t", "c", null).length);

        // 验证委托时参数透传
        verify(columnHandler).getString(state, 1, "t", "c", "v");
        verify(columnHandler).getInt(state, 1, "t", "c", 0);
        verify(columnHandler).getObject(state, 1, "t", "c", "v", String.class);
    }
}
