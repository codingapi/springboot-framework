package com.codingapi.springboot.leaf;

import com.codingapi.springboot.leaf.exception.LeafServerException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
class LeafContext {

    private Leaf leaf;
    @Setter
    private Set<Class<? extends LeafIdGenerate>> classes;

    private LeafContext(){
    }

    private static LeafContext instance;

    public static LeafContext getInstance() {
        if(instance==null){
            synchronized (LeafContext.class){
                if(instance==null){
                    instance = new LeafContext();
                }
            }
        }
        return instance;
    }

    protected void setLeaf(Leaf leaf){
        this.leaf = leaf;
        this.initClass();
    }


    long generateId(Class<?> clazz){
        return segmentGetId(clazz);
    }

    private long segmentGetId(Class<?> clazz){
        return leaf.segmentGetId(clazz.getName());
    }


    boolean push(String key, int step, int maxId){
        return leaf.segmentPush(key,step,maxId);
    }


    private void initClass(){
        if(classes!=null&&classes.size()>0) {
            for (Class<?> clazz : classes) {
                try {
                    LeafContext.getInstance().push(clazz.getName(), 2000, 1);
                } catch (Exception e) {
                    throw new LeafServerException(e);
                }
            }
        }
        leaf.initIdGen();
        log.info("leaf init finish.");
    }

}