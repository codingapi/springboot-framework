package com.codingapi.springboot.framework.persistence;

import lombok.Getter;

/**
 * 持久化事件
 * @param <T>
 */
class PersistenceEvent<T> implements IPersistenceEvent {

    @Getter
    private final T val;

    public PersistenceEvent(T val) {
        this.val = val;
    }
}
