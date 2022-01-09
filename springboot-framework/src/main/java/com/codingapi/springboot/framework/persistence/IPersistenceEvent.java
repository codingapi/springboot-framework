package com.codingapi.springboot.framework.persistence;

import com.codingapi.springboot.framework.event.ISyncEvent;

/**
 *
 * 持久化同步事件
 * 相比于ISyncEvent在IPersistenceEvent执行出现异常的时候会直接抛出
 * @see com.codingapi.springboot.framework.persistence.IPersistence
 */
interface IPersistenceEvent extends ISyncEvent {

}
