package com.codingapi.springboot.generator.dao;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DbHelper<T> {

    private final DataSource dataSource;
    private final QueryRunner queryRunner;


    public DbHelper(DataSource dataSource) {
        this.dataSource = dataSource;
        this.queryRunner = new QueryRunner();
    }

    public void execute(IExecute execute) throws SQLException {
        Connection connection = dataSource.getConnection();
        execute.execute(connection, queryRunner);
        connection.close();
    }

    public T updateAndQuery(IUpdateAndQuery<T> execute) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        T res = execute.updateAndQuery(connection, queryRunner);
        connection.commit();
        connection.close();
        return res;
    }

    public int update(IUpdate execute) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        int res = execute.update(connection, queryRunner);
        connection.commit();
        connection.close();
        return res;
    }

    public T query(IQuery<T> execute) throws SQLException {
        Connection connection = dataSource.getConnection();
        T res = execute.query(connection, queryRunner);
        connection.close();
        return res;
    }


    interface IExecute {
        default void execute(Connection connection, QueryRunner queryRunner) throws SQLException {
        }
    }


    interface IUpdateAndQuery<T> {
        default T updateAndQuery(Connection connection, QueryRunner queryRunner) throws SQLException {
            return null;
        }
    }

    interface IUpdate {
        default int update(Connection connection, QueryRunner queryRunner) throws SQLException {
            return 0;
        }
    }

    interface IQuery<T> {
        default T query(Connection connection, QueryRunner queryRunner) throws SQLException {
            return null;
        }
    }

}
