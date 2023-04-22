package com.codingapi.springboot.generator.dao;

import com.codingapi.springboot.generator.domain.IdKey;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IdKeyDao {

    private final DbHelper<List<IdKey>> dbHelper;

    private final ResultSetHandler<List<IdKey>> handler;

    public IdKeyDao(DataSource dataSource) {
        this.dbHelper = new DbHelper<>(dataSource);
        this.handler = rs -> {
            List<IdKey> list = new ArrayList<>();
            while (rs.next()) {
                IdKey generator = new IdKey();
                generator.setKey(rs.getString("TAG"));
                generator.setId(rs.getInt("ID"));
                generator.setUpdateTime(rs.getLong("UPDATE_TIME"));
                list.add(generator);
            }
            return list;
        };
    }

    @SneakyThrows
    public void save(IdKey generator) {
        dbHelper.update(new DbHelper.IUpdate() {
            @Override
            public int update(Connection connection, QueryRunner queryRunner) throws SQLException {
                List<IdKey> list = queryRunner.query(connection, "SELECT * FROM ID_GENERATOR WHERE TAG = ?", handler, generator.getKey());
                if (list != null && list.size() > 0) {
                    return 0;
                }

                String sql = "INSERT INTO ID_GENERATOR (ID, UPDATE_TIME, TAG) VALUES (?, ?, ?)";
                return queryRunner.update(connection, sql, generator.getId(), generator.getUpdateTime(), generator.getKey());
            }
        });
    }


    @SneakyThrows
    public IdKey getByKey(String key) {
        return dbHelper.query(new DbHelper.IQuery<List<IdKey>>() {
            @Override
            public List<IdKey> query(Connection connection, QueryRunner queryRunner) throws SQLException {
                return queryRunner.query(connection, "SELECT * FROM ID_GENERATOR WHERE TAG = ?", handler, key);
            }
        }).stream().findFirst().orElse(null);
    }


    @SneakyThrows
    public IdKey updateMaxId(IdKey generator) {
        return dbHelper.updateAndQuery(new DbHelper.IUpdateAndQuery<>() {
            @Override
            public List<IdKey> updateAndQuery(Connection connection, QueryRunner queryRunner) throws SQLException {
                queryRunner.update(connection, "UPDATE ID_GENERATOR SET ID = ID + 1 WHERE TAG = ?", generator.getKey());
                return queryRunner.query(connection, "SELECT * FROM ID_GENERATOR WHERE TAG = ?", handler, generator.getKey());
            }
        }).stream().findFirst().orElse(null);
    }


    @SneakyThrows
    public List<IdKey> findAll() throws SQLException {
        return dbHelper.query(new DbHelper.IQuery<>() {
            @Override
            public List<IdKey> query(Connection connection, QueryRunner queryRunner) throws SQLException {
                return queryRunner.query(connection, "SELECT * FROM ID_GENERATOR", handler);
            }
        });
    }

    private void init() throws SQLException {
        dbHelper.execute(new DbHelper.IExecute() {
            @Override
            public void execute(Connection connection, QueryRunner queryRunner) throws SQLException {
                String sql = "CREATE TABLE IF NOT EXISTS ID_GENERATOR (TAG TEXT NOT NULL, ID INTEGER  NOT NULL, UPDATE_TIME INTEGER NOT NULL, PRIMARY KEY (TAG))";
                queryRunner.update(connection, sql);
            }
        });
    }
}
