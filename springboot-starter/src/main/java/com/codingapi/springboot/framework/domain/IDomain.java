package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.domain.event.DomainPersistEvent;
import com.codingapi.springboot.framework.event.EventPusher;

/**
 * 领域对象事件
 */
public interface IDomain  {

    /**
     * 持久化事件
     */
    default void persist(){
        EventPusher.push(new DomainPersistEvent(this));
    }
}
