package com.codingapi.springboot.authorization.handler;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * ResultSet 拦截器
 */
@Setter
public class ColumnHandlerContext {

    @Getter
    private final static ColumnHandlerContext instance = new ColumnHandlerContext();

    private ColumnHandlerContext() {
        this.columnHandler = new DefaultColumnHandler();
    }

    private ColumnHandler columnHandler;


    public String getString(int columnIndex, String tableName, String columnName, String value) {
        return columnHandler.getString(columnIndex, tableName, columnName, value);
    }

    public short getShort(int columnIndex, String tableName, String columnName, short value) {
        return columnHandler.getShort(columnIndex, tableName, columnName, value);
    }

    public boolean getBoolean(int columnIndex, String tableName, String columnName, boolean value) {
        return columnHandler.getBoolean(columnIndex, tableName, columnName, value);
    }

    public byte getByte(int columnIndex, String tableName, String columnName, byte value) {
        return columnHandler.getByte(columnIndex, tableName, columnName, value);
    }

    public int getInt(int columnIndex, String tableName, String columnName, int value) {
        return columnHandler.getInt(columnIndex, tableName, columnName, value);
    }

    public long getLong(int columnIndex, String tableName, String columnName, long value) {
        return columnHandler.getLong(columnIndex, tableName, columnName, value);
    }

    public float getFloat(int columnIndex, String tableName, String columnName, float value) {
        return columnHandler.getFloat(columnIndex, tableName, columnName, value);
    }

    public double getDouble(int columnIndex, String tableName, String columnName, double value) {
        return columnHandler.getDouble(columnIndex, tableName, columnName, value);
    }

    public BigDecimal getBigDecimal(int columnIndex, String tableName, String columnName, BigDecimal value) {
        return columnHandler.getBigDecimal(columnIndex, tableName, columnName, value);
    }

    public byte[] getBytes(int columnIndex, String tableName, String columnName, byte[] value) {
        return columnHandler.getBytes(columnIndex, tableName, columnName, value);
    }

    public Timestamp getTimestamp(int columnIndex, String tableName, String columnName, Timestamp value) {
        return columnHandler.getTimestamp(columnIndex, tableName, columnName, value);
    }

    public Time getTime(int columnIndex, String tableName, String columnName, Time value) {
        return columnHandler.getTime(columnIndex, tableName, columnName, value);
    }

    public Date getDate(int columnIndex, String tableName, String columnName, Date value) {
        return columnHandler.getDate(columnIndex, tableName, columnName, value);
    }

    public InputStream getAsciiStream(int columnIndex, String tableName, String columnName, InputStream value) {
        return columnHandler.getAsciiStream(columnIndex, tableName, columnName, value);
    }

    public InputStream getUnicodeStream(int columnIndex, String tableName, String columnName, InputStream value) {
        return columnHandler.getUnicodeStream(columnIndex, tableName, columnName, value);
    }

    public InputStream getBinaryStream(int columnIndex, String tableName, String columnName, InputStream value) {
        return columnHandler.getBinaryStream(columnIndex, tableName, columnName, value);
    }

    public Object getObject(int columnIndex, String tableName, String columnName, Object value) {
        return columnHandler.getObject(columnIndex, tableName, columnName, value);
    }

    public Reader getCharacterStream(int columnIndex, String tableName, String columnName, Reader value) {
        return columnHandler.getCharacterStream(columnIndex, tableName, columnName, value);
    }
}
