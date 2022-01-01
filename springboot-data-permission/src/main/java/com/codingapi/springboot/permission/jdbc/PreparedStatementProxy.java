package com.codingapi.springboot.permission.jdbc;

import com.codingapi.springboot.permission.analyzer.JdbcSql;
import com.codingapi.springboot.permission.analyzer.SQL;
import com.codingapi.springboot.permission.event.JdbcPreparedStatementExecuteEvent;
import com.codingapi.springboot.framework.event.DomainEventContext;
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
public class PreparedStatementProxy extends StatementProxy implements PreparedStatement{

    private final PreparedStatement delegate;

    private final JdbcSql jdbcSql;

    public PreparedStatementProxy(PreparedStatement delegate, SQL sql) throws SQLException{
        super(delegate);
        this.delegate = delegate;
        this.jdbcSql = new JdbcSql(sql);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        log.debug("jdbcSql:{}",jdbcSql.getExecuteSql());
        DomainEventContext.getInstance().push(new JdbcPreparedStatementExecuteEvent(delegate,jdbcSql));
        return delegate.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        log.debug("jdbcSql:{}",jdbcSql.getExecuteSql());
        DomainEventContext.getInstance().push(new JdbcPreparedStatementExecuteEvent(delegate,jdbcSql));
        return delegate.executeUpdate();
    }

    @Override
    public boolean execute() throws SQLException {
        log.debug("jdbcSql:{}",jdbcSql.getExecuteSql());
        DomainEventContext.getInstance().push(new JdbcPreparedStatementExecuteEvent(delegate,jdbcSql));
        return delegate.execute();
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

}
