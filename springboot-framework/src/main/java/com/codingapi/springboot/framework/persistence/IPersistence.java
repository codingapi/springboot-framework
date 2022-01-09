package com.codingapi.springboot.framework.persistence;

import com.codingapi.springboot.framework.event.EventPusher;

/**
 * 可持久化的对象
 */
public interface IPersistence {

    /**
     * 持久化对象
     * @see IPersistenceRepository
     */
    default void persistence(){
        EventPusher.push(new PersistenceEvent(this));
    }
}
