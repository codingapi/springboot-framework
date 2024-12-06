package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.DataAuthorizationContext;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 *  默认ResultSet 拦截器
 */
public class DefaultColumnHandler implements ColumnHandler {

    @Override
    public String getString(int columnIndex, String tableName, String columnName, String value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public boolean getBoolean(int columnIndex, String tableName, String columnName, boolean value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public byte getByte(int columnIndex, String tableName, String columnName, byte value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public short getShort(int columnIndex, String tableName, String columnName, short value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public int getInt(int columnIndex, String tableName, String columnName, int value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public long getLong(int columnIndex, String tableName, String columnName, long value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public float getFloat(int columnIndex, String tableName, String columnName, float value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public double getDouble(int columnIndex, String tableName, String columnName, double value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, String tableName, String columnName, BigDecimal value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public byte[] getBytes(int columnIndex, String tableName, String columnName, byte[] value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public Date getDate(int columnIndex, String tableName, String columnName, Date value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public Time getTime(int columnIndex, String tableName, String columnName, Time value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, String tableName, String columnName, Timestamp value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex, String tableName, String columnName, InputStream value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex, String tableName, String columnName, InputStream value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex, String tableName, String columnName, InputStream value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public Object getObject(int columnIndex, String tableName, String columnName, Object value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }

    @Override
    public Reader getCharacterStream(int columnIndex, String tableName, String columnName, Reader value) {
        return DataAuthorizationContext.getInstance().columnAuthorization(tableName, columnName, value);
    }
}
