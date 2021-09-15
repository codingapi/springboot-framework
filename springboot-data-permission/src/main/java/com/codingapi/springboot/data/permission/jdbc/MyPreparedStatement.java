package com.codingapi.springboot.data.permission.jdbc;

import com.codingapi.springboot.data.permission.sql.JdbcSql;
import com.codingapi.springboot.data.permission.sql.SQL;
import com.codingapi.springboot.data.permission.sql.event.JdbcExecuteEvent;
import com.codingapi.springboot.framework.event.ApplicationEventUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class MyPreparedStatement extends AbsWrapper implements PreparedStatement{

    private final PreparedStatement delegate;

    private final JdbcSql jdbcSql;

    public MyPreparedStatement(PreparedStatement delegate,SQL sql) throws SQLException{
        super(delegate);
        this.delegate = delegate;
        this.jdbcSql = new JdbcSql(sql);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return delegate.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        log.info("jdbcSql:{}",jdbcSql.getExecuteSql());
        ApplicationEventUtils.getInstance().push(new JdbcExecuteEvent(delegate,jdbcSql));
        return delegate.executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        jdbcSql.put(parameterIndex,null,(index)->{
            delegate.setNull(index, sqlType);
        });
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBoolean(index, x);
        });
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)-> {
            delegate.setByte(index, x);
        });
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setShort(index, x);
        });
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setInt(index, x);
        });
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setLong(index, x);
        });
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setFloat(index, x);
        });
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setDouble(index, x);
        });
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBigDecimal(index, x);
        });
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setString(index, x);
        });
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBytes(index, x);
        });
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setDate(index, x);
        });
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setTime(index, x);
        });
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setTimestamp(index, x);
        });
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setAsciiStream(index, x);
        });
    }

    @Override
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setUnicodeStream(index, x,length);
        });
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBinaryStream(index, x,length);
        });
    }

    @Override
    public void clearParameters() throws SQLException {
        jdbcSql.clearParameters(delegate::clearParameters);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setObject(index, x, targetSqlType);
        });
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setObject(index, x);
        });
    }

    @Override
    public boolean execute() throws SQLException {
        return delegate.execute();
    }

    @Override
    public void addBatch() throws SQLException {
        delegate.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setCharacterStream(index, reader, length);
        });
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setRef(index, x);
        });
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBlob(index, x);
        });
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setClob(index, x);
        });
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setArray(index, x);
        });
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setDate(index, x, cal);
        });
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setTime(index, x, cal);
        });
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setTimestamp(index, x, cal);
        });
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        jdbcSql.put(parameterIndex,null,(index)->{
            delegate.setNull(index, sqlType, typeName);
        });
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setURL(index, x);
        });
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return delegate.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setRowId(index, x);
        });
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        jdbcSql.put(parameterIndex,value,(index)->{
            delegate.setNString(index, value);
        });
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        jdbcSql.put(parameterIndex,value,(index)->{
            delegate.setNCharacterStream(index, value, length);
        });
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        jdbcSql.put(parameterIndex,value,(index)->{
            delegate.setNClob(index, value);
        });
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setClob(index, reader, length);
        });
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        jdbcSql.put(parameterIndex,inputStream,(index)->{
            delegate.setBlob(index, inputStream, length);
        });
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setNClob(index, reader, length);
        });
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        jdbcSql.put(parameterIndex,xmlObject,(index)->{
            delegate.setSQLXML(index, xmlObject);
        });
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setObject(index, x, targetSqlType, scaleOrLength);
        });
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setAsciiStream(index, x, length);
        });
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBinaryStream(index, x, length);
        });
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setCharacterStream(index, reader, length);
        });
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setAsciiStream(index, x);
        });
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        jdbcSql.put(parameterIndex,x,(index)->{
            delegate.setBinaryStream(index, x);
        });
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setCharacterStream(index, reader);
        });
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        jdbcSql.put(parameterIndex,value,(index)->{
            delegate.setNCharacterStream(index, value);
        });
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setClob(index, reader);
        });
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        jdbcSql.put(parameterIndex,inputStream,(index)->{
            delegate.setBlob(index, inputStream);
        });
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        jdbcSql.put(parameterIndex,reader,(index)->{
            delegate.setNClob(index, reader);
        });
    }


    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return delegate.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return delegate.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        delegate.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return delegate.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        delegate.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return delegate.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        delegate.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        delegate.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return delegate.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        delegate.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        delegate.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        delegate.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return delegate.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return delegate.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return delegate.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return delegate.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        delegate.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return delegate.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        delegate.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return delegate.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return delegate.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return delegate.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        delegate.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        delegate.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return delegate.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return delegate.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return delegate.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return delegate.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return delegate.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return delegate.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return delegate.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return delegate.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return delegate.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return delegate.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        delegate.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return delegate.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        delegate.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return delegate.isCloseOnCompletion();
    }

}
