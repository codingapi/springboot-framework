package com.codingapi.springboot.authorization.handler;

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

    String getString(int columnIndex, String tableName, String columnName, String value);

    boolean getBoolean(int columnIndex, String tableName, String columnName, boolean value);

    byte getByte(int columnIndex, String tableName, String columnName, byte value);

    short getShort(int columnIndex, String tableName, String columnName, short value);

    int getInt(int columnIndex, String tableName, String columnName, int value);

    long getLong(int columnIndex, String tableName, String columnName, long value);

    float getFloat(int columnIndex, String tableName, String columnName, float value);

    double getDouble(int columnIndex, String tableName, String columnName, double value);

    BigDecimal getBigDecimal(int columnIndex, String tableName, String columnName, BigDecimal value);

    byte[] getBytes(int columnIndex, String tableName, String columnName, byte[] value);

    Date getDate(int columnIndex, String tableName, String columnName, Date value);

    Time getTime(int columnIndex, String tableName, String columnName, Time value);

    Timestamp getTimestamp(int columnIndex, String tableName, String columnName, Timestamp value);

    InputStream getAsciiStream(int columnIndex, String tableName, String columnName, InputStream value);

    InputStream getUnicodeStream(int columnIndex, String tableName, String columnName, InputStream value);

    InputStream getBinaryStream(int columnIndex, String tableName, String columnName, InputStream value);

    Object getObject(int columnIndex, String tableName, String columnName, Object value);

    Reader getCharacterStream(int columnIndex, String tableName, String columnName, Reader value);

}
