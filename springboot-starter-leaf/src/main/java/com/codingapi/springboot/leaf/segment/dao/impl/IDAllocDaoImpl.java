package com.codingapi.springboot.leaf.segment.dao.impl;

import com.codingapi.springboot.leaf.h2.DataHelper;
import com.codingapi.springboot.leaf.segment.dao.IDAllocDao;
import com.codingapi.springboot.leaf.segment.model.LeafAlloc;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class IDAllocDaoImpl implements IDAllocDao {

    private DataHelper dataHelper;

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        //todo leaf
        String sql = "select * from leaf_alloc";
        return new ArrayList<>();
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        //todo leaf
        //UPDATE leaf_alloc SET maxId = maxId + step WHERE key = :key

        //select * from leaf_alloc where key = :key
        LeafAlloc leafAlloc = new LeafAlloc();
        return leafAlloc;
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        //UPDATE leaf_alloc SET maxId = maxId + :step WHERE key = :key

        //select * from leaf_alloc where key = :key
        //todo leaf
        return leafAlloc;
    }

    @Override
    public List<String> getAllTags() {
        String sql = "select tag from leaf_alloc";
        //todo leaf
        return Arrays.asList("tags");
    }

    @Override
    public boolean save(LeafAlloc leafInfo) {
        String sql = "insert into leaf_alloc (max_id, step, update_time, tag) values (?, ?, ?, ?)";
        //todo leaf
        return true;
    }

    public void init(){
        String sql = "create table leaf_alloc (tag varchar(128) not null, max_id bigint, step integer, update_time timestamp, primary key (tag))";

    }
}
