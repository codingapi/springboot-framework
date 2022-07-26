package com.codingapi.springboot.leaf.service;

import com.codingapi.springboot.leaf.IDGen;
import com.codingapi.springboot.leaf.common.Result;
import com.codingapi.springboot.leaf.exception.InitException;
import com.codingapi.springboot.leaf.segment.SegmentIDGenImpl;
import com.codingapi.springboot.leaf.segment.dao.IDAllocDao;
import com.codingapi.springboot.leaf.segment.model.LeafAlloc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SegmentService {

    private final IDGen idGen;

    private final IDAllocDao dao;

    public SegmentService(IDAllocDao dao) throws InitException {
        this.dao = dao;
        // Config ID Gen
        idGen = new SegmentIDGenImpl(dao);
        if (idGen.init()) {
            log.info("Segment Service Init Successfully");
        } else {
            throw new InitException("Segment Service Init Fail");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }

    public void save(LeafAlloc leafAlloc) throws InitException{
        if(dao==null){
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        boolean flag =  dao.save(leafAlloc);
        if(!flag){
            log.warn("Segment Service Running..");
            return;
        }
        if (idGen.init()) {
            log.info("Segment Service Init Successfully");
        } else {
            throw new InitException("Segment Service Init Fail");
        }
    }
}
