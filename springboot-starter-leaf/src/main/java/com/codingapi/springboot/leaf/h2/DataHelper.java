package com.codingapi.springboot.leaf.h2;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataHelper {

    private DataSource dataSource;

    private QueryRunner queryRunner;

    public DataHelper(DataSource dataSource) {
        this.dataSource = dataSource;
        this.queryRunner = new QueryRunner();
    }

    public void execute(String sql,Object...params) throws DbException {
        try {
            Connection connection = dataSource.getConnection();
            queryRunner.execute(connection,sql,params);
            connection.close();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public void update(String sql,Object...params) throws DbException {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            queryRunner.update(connection,sql,params);
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public <T> void query(String sql, ResultSetHandler<T> handler, Object...params)throws DbException{
        try {
            Connection connection = dataSource.getConnection();
            queryRunner.query(connection,sql,handler,params);
            connection.close();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }




}
