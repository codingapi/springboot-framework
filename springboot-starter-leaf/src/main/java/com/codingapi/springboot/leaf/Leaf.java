package com.codingapi.springboot.leaf;


import com.codingapi.springboot.leaf.exception.LeafServerException;
import com.codingapi.springboot.leaf.exception.NoKeyException;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
class Leaf {

    private final IDGen idGen;
    private final IDAllocDao idAllocDao;


    long segmentGetId(String key){
        Result result =  idGen.get(key);
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }
        return result.getId();
    }


    /**
     * 数据库模式添加 数据
     * @param key   key 关键字
     * @param step  每次获取数据长度 2000
     * @param maxId 开始的最大Id 1
     * @return  执行状态
     */
    boolean segmentPush(String key, int step, int maxId)  {
        LeafAlloc alloc = new LeafAlloc();
        alloc.setKey(key);
        alloc.setStep(step);
        alloc.setMaxId(maxId);
        alloc.setUpdateTime(new Date());
        return idAllocDao.save(alloc);
    }

    void initIdGen(){
        idGen.init();
    }

     void init(){
        LeafContext.getInstance().setLeaf(this);
    }

}

