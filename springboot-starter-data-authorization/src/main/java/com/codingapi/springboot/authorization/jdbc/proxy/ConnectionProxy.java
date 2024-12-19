package com.codingapi.springboot.authorization.jdbc.proxy;

import com.codingapi.springboot.authorization.interceptor.SQLInterceptState;
import com.codingapi.springboot.authorization.interceptor.SQLRunningContext;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConnectionProxy implements Connection {

    private final Connection connection;

    public ConnectionProxy(Connection connection) {
        this.connection = connection;
    }

    private SQLInterceptState interceptState;

    @Override
    public Statement createStatement() throws SQLException {
        return new StatementProxy(connection.createStatement(), interceptState);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(connection.prepareStatement(interceptState.getSql()),interceptState);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new CallableStatementProxy(connection.prepareCall(interceptState.getSql()), interceptState);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return connection.nativeSQL(interceptState.getSql());
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new StatementProxy(connection.createStatement(resultSetType, resultSetConcurrency),this.interceptState);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(connection.prepareStatement(interceptState.getSql(), resultSetType, resultSetConcurrency),interceptState);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new CallableStatementProxy(connection.prepareCall(interceptState.getSql(), resultSetType, resultSetConcurrency),interceptState);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new StatementProxy(connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability),interceptState);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(connection.prepareStatement(interceptState.getSql(), resultSetType, resultSetConcurrency, resultSetHoldability),interceptState);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new CallableStatementProxy(connection.prepareCall(interceptState.getSql(), resultSetType, resultSetConcurrency, resultSetHoldability),interceptState);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(connection.prepareStatement(interceptState.getSql(), autoGeneratedKeys),interceptState);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(connection.prepareStatement(interceptState.getSql(), columnIndexes),interceptState);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(connection.prepareStatement(interceptState.getSql(), columnNames),interceptState);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
}
