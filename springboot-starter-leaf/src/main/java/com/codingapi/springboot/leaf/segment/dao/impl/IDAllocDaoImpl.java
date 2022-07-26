package com.codingapi.springboot.leaf.segment.dao.impl;

import com.codingapi.springboot.leaf.segment.dao.IDAllocDao;
import com.codingapi.springboot.leaf.segment.model.LeafAlloc;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class IDAllocDaoImpl implements IDAllocDao {

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        //todo leaf
        return new ArrayList<>();
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        //todo leaf
        LeafAlloc leafAlloc = new LeafAlloc();
        return leafAlloc;
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        //todo leaf
        return leafAlloc;
    }

    @Override
    public List<String> getAllTags() {
        //todo leaf
        return Arrays.asList("tags");
    }

    @Override
    public boolean save(LeafAlloc leafInfo) {
        //todo leaf
        return true;
    }
}
