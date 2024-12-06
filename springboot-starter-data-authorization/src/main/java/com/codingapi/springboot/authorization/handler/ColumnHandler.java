package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.interceptor.SQLInterceptState;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 列表拦截器
 */
public interface ColumnHandler {

    String getString(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, String value);

    boolean getBoolean(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, boolean value);

    byte getByte(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, byte value);

    short getShort(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, short value);

    int getInt(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, int value);

    long getLong(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, long value);

    float getFloat(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, float value);

    double getDouble(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, double value);

    BigDecimal getBigDecimal(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, BigDecimal value);

    byte[] getBytes(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, byte[] value);

    Date getDate(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Date value);

    Time getTime(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Time value);

    Timestamp getTimestamp(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Timestamp value);

    InputStream getAsciiStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value);

    InputStream getUnicodeStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value);

    InputStream getBinaryStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value);

    Object getObject(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Object value);

    Reader getCharacterStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Reader value);

}
