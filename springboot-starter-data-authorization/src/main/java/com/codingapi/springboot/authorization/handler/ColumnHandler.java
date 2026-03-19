package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.interceptor.SQLExecuteState;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

/**
 * 列表拦截器
 */
public interface ColumnHandler {

    String getString(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, String value);

    boolean getBoolean(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, boolean value);

    byte getByte(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, byte value);

    short getShort(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, short value);

    int getInt(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, int value);

    long getLong(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, long value);

    float getFloat(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, float value);

    double getDouble(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, double value);

    BigDecimal getBigDecimal(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, BigDecimal value);

    byte[] getBytes(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, byte[] value);

    Date getDate(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Date value);

    Time getTime(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Time value);

    Timestamp getTimestamp(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Timestamp value);

    InputStream getAsciiStream(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, InputStream value);

    InputStream getUnicodeStream(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, InputStream value);

    InputStream getBinaryStream(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, InputStream value);

    Object getObject(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Object value);

    Reader getCharacterStream(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Reader value);

    Ref getRef(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Ref value);

    Blob getBlob(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Blob value);

    Clob getClob(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Clob value);

    Array getArray(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Array value);

    URL getURL(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, URL value);

    NClob getNClob(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, NClob value);

    SQLXML getSQLXML(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, SQLXML value);

    String getNString(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, String value);

    Reader getNCharacterStream(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, Reader value);

    RowId getRowId(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, RowId value);

    <T> T getObject(SQLExecuteState interceptState, int columnIndex, String tableName, String columnName, T value, Class<T> type);

}
