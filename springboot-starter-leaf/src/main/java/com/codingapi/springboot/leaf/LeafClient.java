package com.codingapi.springboot.leaf;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class LeafClient {

    public long segmentGetId(String key){
        //todo leaf
        return 1;
    }


    /**
     * 数据库模式添加 数据
     * @param key   key 关键字
     * @param step  每次获取数据长度 2000
     * @param maxId 开始的最大Id 1
     * @return  执行状态
     */
    public boolean segmentPush(String key, int step, int maxId){
        //todo leaf
        return true;
    }

    public void init(){
        LeafUtils.getInstance().setLeafClient(this);
    }

}
