package com.codingapi.springboot.leaf;

import com.codingapi.springboot.leaf.exception.LeafServerException;
import com.codingapi.springboot.leaf.properties.LeafProperties;
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
    private int defaultStep;
    private int defaultMaxId;

    @Setter
    private Set<Class<? extends LeafIdGenerate>> classes;

    private LeafContext() {
    }

    private static LeafContext instance;

    public static LeafContext getInstance() {
        if (instance == null) {
            synchronized (LeafContext.class) {
                if (instance == null) {
                    instance = new LeafContext();
                }
            }
        }
        return instance;
    }

    protected void setLeaf(Leaf leaf, LeafProperties leafProperties) {
        this.leaf = leaf;
        this.defaultMaxId = leafProperties.getDefaultMaxId();
        this.defaultStep = leafProperties.getDefaultStep();
        this.initClass();
    }


    long generateId(Class<?> clazz) {
        return segmentGetId(clazz);
    }

    long segmentGetId(Class<?> clazz) {
        return leaf.segmentGetId(clazz.getName());
    }


    public void push(String key, int step, int maxId) {
        leaf.segmentPush(key, step, maxId);
    }


    private void initClass() {
        if (classes != null && classes.size() > 0) {
            for (Class<?> clazz : classes) {
                try {
                    LeafContext.getInstance().push(clazz.getName(), defaultStep, defaultMaxId);
                } catch (Exception e) {
                    throw new LeafServerException(e);
                }
            }
        }
        leaf.initIdGen();
        log.info("leaf init finish.");
    }

}