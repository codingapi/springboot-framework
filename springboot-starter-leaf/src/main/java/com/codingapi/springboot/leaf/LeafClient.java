package com.codingapi.springboot.leaf;

import com.codingapi.springboot.leaf.common.Result;
import com.codingapi.springboot.leaf.common.Status;
import com.codingapi.springboot.leaf.exception.InitException;
import com.codingapi.springboot.leaf.exception.LeafServerException;
import com.codingapi.springboot.leaf.exception.NoKeyException;
import com.codingapi.springboot.leaf.segment.model.LeafAlloc;
import com.codingapi.springboot.leaf.service.SegmentService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class LeafClient {

    private SegmentService segmentService;

    public LeafClient(SegmentService segmentService) {
        this.segmentService = segmentService;
    }

    public long segmentGetId(String key){
        Result result =  segmentService.getId(key);
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
    public boolean segmentPush(String key, int step, int maxId) throws InitException {
        //todo leaf
        LeafAlloc alloc = new LeafAlloc();
        alloc.setKey(key);
        alloc.setStep(step);
        alloc.setMaxId(maxId);
        alloc.setUpdateTime(new Date());
        segmentService.save(alloc);
        return true;
    }

    public void init(){
        LeafUtils.getInstance().setLeafClient(this);
    }

}
