package com.sankuai.inf.leaf.segment.dao.impl;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IDAllocDaoImpl implements IDAllocDao {

    private final DbHelper<List<LeafAlloc>> dbHelper;

    private final ResultSetHandler<List<LeafAlloc>> handler;

    public IDAllocDaoImpl(String jdbcUrl) {
        this.handler = rs -> {
            List<LeafAlloc> list = new ArrayList<>();
            while (rs.next()){
                LeafAlloc leafAlloc = new LeafAlloc();
                leafAlloc.setStep(rs.getInt("STEP"));
                leafAlloc.setKey(rs.getString("TAG"));
                leafAlloc.setMaxId(rs.getInt("MAX_ID"));
                leafAlloc.setUpdateTime(rs.getDate("UPDATE_TIME"));
                list.add(leafAlloc);
            }
            return list;
        };
        this.dbHelper = new DbHelper<>(handler,jdbcUrl);
        this.init();
    }

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        return dbHelper.query(new DbHelper.IQuery<List<LeafAlloc>>() {
            @Override
            public List<LeafAlloc> query(Connection connection, QueryRunner queryRunner)throws SQLException {
                return queryRunner.query(connection,"SELECT * FROM LEAF_ALLOC",handler);
            }
        });
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        return dbHelper.updateAndQuery(new DbHelper.IUpdateAndQuery<List<LeafAlloc>>() {
            @Override
            public List<LeafAlloc> updateAndQuery(Connection connection, QueryRunner queryRunner) throws SQLException {
                queryRunner.update(connection,"UPDATE LEAF_ALLOC SET MAX_ID = MAX_ID + STEP WHERE TAG = ?",tag);
                return queryRunner.query(connection,"SELECT * FROM LEAF_ALLOC WHERE TAG = ?",handler,tag);
            }
        }).stream().findFirst().orElse(null);

    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        return dbHelper.updateAndQuery(new DbHelper.IUpdateAndQuery<List<LeafAlloc>>() {
            @Override
            public List<LeafAlloc> updateAndQuery(Connection connection, QueryRunner queryRunner) throws SQLException {
                queryRunner.update(connection,"UPDATE LEAF_ALLOC SET MAX_ID = MAX_ID + ? WHERE TAG = ?",leafAlloc.getStep(),leafAlloc.getKey());
                return queryRunner.query(connection,"SELECT * FROM LEAF_ALLOC WHERE TAG = ?",handler,leafAlloc.getKey());
            }
        }).stream().findFirst().orElse(null);
    }


    @Override
    public List<String> getAllTags() {
        return getAllLeafAllocs().stream().map(LeafAlloc::getKey).collect(Collectors.toList());
    }


    @Override
    public boolean save(LeafAlloc leafAlloc){
        return dbHelper.update(new DbHelper.IUpdate() {
            @Override
            public int update(Connection connection, QueryRunner queryRunner) throws SQLException {
                String sql = "INSERT INTO LEAF_ALLOC (MAX_ID, STEP, UPDATE_TIME, TAG) VALUES (?, ?, ?, ?)";
                List<LeafAlloc> list =  queryRunner.query(connection,"SELECT * FROM LEAF_ALLOC WHERE TAG = ?",handler,leafAlloc.getKey());
                if(list!=null&&list.size()>0){
                    return 0;
                }
                return queryRunner.update(connection,sql,leafAlloc.getMaxId(),leafAlloc.getStep(),leafAlloc.getUpdateTime(),leafAlloc.getKey());
            }
        })>0;
    }


    private void init(){
        dbHelper.execute(new DbHelper.IExecute() {
            @Override
            public void execute(Connection connection, QueryRunner queryRunner) throws SQLException {
                String sql = "CREATE TABLE IF NOT EXISTS LEAF_ALLOC (TAG VARCHAR(128) NOT NULL, MAX_ID BIGINT, STEP INTEGER, UPDATE_TIME TIMESTAMP, PRIMARY KEY (TAG))";
                queryRunner.execute(connection,sql);
            }
        });
    }

}
