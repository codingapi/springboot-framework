package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.interceptor.SQLInterceptState;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

/**
 *  默认ResultSet 拦截器
 */
public class DefaultColumnHandler implements ColumnHandler {

    @Override
    public String getString(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, String value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public boolean getBoolean(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, boolean value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public byte getByte(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, byte value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public short getShort(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, short value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public int getInt(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, int value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public long getLong(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, long value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public float getFloat(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, float value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public double getDouble(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, double value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public BigDecimal getBigDecimal(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, BigDecimal value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public byte[] getBytes(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, byte[] value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Date getDate(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Date value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Time getTime(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Time value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Timestamp getTimestamp(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Timestamp value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public InputStream getAsciiStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public InputStream getUnicodeStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public InputStream getBinaryStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Object getObject(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Object value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Reader getCharacterStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Reader value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Ref getRef(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Ref value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Blob getBlob(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Blob value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Clob getClob(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Clob value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Array getArray(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Array value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public URL getURL(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, URL value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public NClob getNClob(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, NClob value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public SQLXML getSQLXML(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, SQLXML value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public String getNString(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, String value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public Reader getNCharacterStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Reader value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public RowId getRowId(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, RowId value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }

    @Override
    public <T> T getObject(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, T value, Class<T> type) {
        return DataAuthorizationContext.getInstance().columnAuthorization(interceptState,tableName, columnName, value);
    }
}
