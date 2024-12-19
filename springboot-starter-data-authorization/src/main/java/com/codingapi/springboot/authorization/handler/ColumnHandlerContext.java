package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.interceptor.SQLInterceptState;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

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


    public String getString(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, String value) {
        return columnHandler.getString(interceptState, columnIndex, tableName, columnName, value);
    }

    public short getShort(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, short value) {
        return columnHandler.getShort(interceptState, columnIndex, tableName, columnName, value);
    }

    public boolean getBoolean(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, boolean value) {
        return columnHandler.getBoolean(interceptState, columnIndex, tableName, columnName, value);
    }

    public byte getByte(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, byte value) {
        return columnHandler.getByte(interceptState, columnIndex, tableName, columnName, value);
    }

    public int getInt(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, int value) {
        return columnHandler.getInt(interceptState, columnIndex, tableName, columnName, value);
    }

    public long getLong(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, long value) {
        return columnHandler.getLong(interceptState, columnIndex, tableName, columnName, value);
    }

    public float getFloat(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, float value) {
        return columnHandler.getFloat(interceptState, columnIndex, tableName, columnName, value);
    }

    public double getDouble(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, double value) {
        return columnHandler.getDouble(interceptState, columnIndex, tableName, columnName, value);
    }

    public BigDecimal getBigDecimal(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, BigDecimal value) {
        return columnHandler.getBigDecimal(interceptState, columnIndex, tableName, columnName, value);
    }

    public byte[] getBytes(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, byte[] value) {
        return columnHandler.getBytes(interceptState, columnIndex, tableName, columnName, value);
    }

    public Timestamp getTimestamp(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Timestamp value) {
        return columnHandler.getTimestamp(interceptState, columnIndex, tableName, columnName, value);
    }

    public Time getTime(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Time value) {
        return columnHandler.getTime(interceptState, columnIndex, tableName, columnName, value);
    }

    public Date getDate(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Date value) {
        return columnHandler.getDate(interceptState, columnIndex, tableName, columnName, value);
    }

    public InputStream getAsciiStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value) {
        return columnHandler.getAsciiStream(interceptState, columnIndex, tableName, columnName, value);
    }

    public InputStream getUnicodeStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value) {
        return columnHandler.getUnicodeStream(interceptState, columnIndex, tableName, columnName, value);
    }

    public InputStream getBinaryStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, InputStream value) {
        return columnHandler.getBinaryStream(interceptState, columnIndex, tableName, columnName, value);
    }

    public Object getObject(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Object value) {
        return columnHandler.getObject(interceptState, columnIndex, tableName, columnName, value);
    }

    public Reader getCharacterStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Reader value) {
        return columnHandler.getCharacterStream(interceptState, columnIndex, tableName, columnName, value);
    }

    public Ref getRef(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Ref value) {
        return columnHandler.getRef(interceptState, columnIndex, tableName, columnName, value);
    }

    public Blob getBlob(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Blob value) {
        return columnHandler.getBlob(interceptState, columnIndex, tableName, columnName, value);
    }

    public Clob getClob(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Clob value) {
        return columnHandler.getClob(interceptState, columnIndex, tableName, columnName, value);
    }

    public Array getArray(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Array value) {
        return columnHandler.getArray(interceptState, columnIndex, tableName, columnName, value);
    }

    public URL getURL(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, URL value) {
        return columnHandler.getURL(interceptState, columnIndex, tableName, columnName, value);
    }

    public NClob getNClob(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, NClob value) {
        return columnHandler.getNClob(interceptState, columnIndex, tableName, columnName, value);
    }

    public SQLXML getSQLXML(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, SQLXML value) {
        return columnHandler.getSQLXML(interceptState, columnIndex, tableName, columnName, value);
    }

    public String getNString(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, String value) {
        return columnHandler.getNString(interceptState, columnIndex, tableName, columnName, value);
    }

    public Reader getNCharacterStream(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, Reader value) {
        return columnHandler.getNCharacterStream(interceptState, columnIndex, tableName, columnName, value);
    }

    public RowId getRowId(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, RowId value) {
        return columnHandler.getRowId(interceptState, columnIndex, tableName, columnName, value);
    }

    public <T> T getObject(SQLInterceptState interceptState, int columnIndex, String tableName, String columnName, T value, Class<T> type) {
        return columnHandler.getObject(interceptState, columnIndex, tableName, columnName, value, type);
    }
}
