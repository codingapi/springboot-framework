package com.codingapi.springboot.framework.persistence;

/**
 * 持久化 存储对象
 * @param <T> 对象类型
 */
public interface IPersistenceRepository<T> {

    /**
     * 持久化回调函数
     * @param t 对象
     */
    void persistenceHandler(T t);

}
