package com.sankuai.inf.leaf.segment.dao.impl;

import com.sankuai.inf.leaf.exception.DbException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DbHelper<T> {

    private final JdbcDataSource dataSource;
    private final QueryRunner queryRunner;
    private final ResultSetHandler<T> handler;


    interface IExecute{
        default void execute(Connection connection,QueryRunner queryRunner)throws SQLException{}
    }

    interface IUpdateAndQuery<T>{
        default T updateAndQuery(Connection connection,QueryRunner queryRunner)throws SQLException{return null;}
    }

    interface IUpdate{
        default int update(Connection connection,QueryRunner queryRunner)throws SQLException{return 0;}
    }

    interface IQuery<T>{
        default T query(Connection connection,QueryRunner queryRunner) throws SQLException{return null;}
    }

    public DbHelper(ResultSetHandler<T> handler,String jdbcUrl) {
        this.dataSource = new JdbcDataSource();
        this.queryRunner = new QueryRunner();
        this.handler = handler;

        dataSource.setURL(jdbcUrl);

    }


    public void execute(IExecute execute) throws DbException{
        try {
            Connection connection = dataSource.getConnection();
            execute.execute(connection,queryRunner);
            connection.close();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }


    public T updateAndQuery(IUpdateAndQuery<T> execute) throws DbException {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            T res = execute.updateAndQuery(connection,queryRunner);
            connection.commit();
            connection.close();
            return res;
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public int update(IUpdate execute) throws DbException {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            int res = execute.update(connection,queryRunner);
            connection.commit();
            connection.close();
            return res;
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public T query(IQuery<T> execute)throws DbException{
        try {
            Connection connection = dataSource.getConnection();
            T res = execute.query(connection,queryRunner);
            connection.close();
            return res;
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

}
