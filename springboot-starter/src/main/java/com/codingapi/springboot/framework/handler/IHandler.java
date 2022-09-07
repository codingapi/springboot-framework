package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IEvent;

/**
 * handler 订阅
 *
 * @param <T> Event 类型
 */
public interface IHandler<T extends IEvent> {

    /**
     * 订阅触发
     *
     * @param event 触发event
     */
    void handler(T event);

    /**
     * 异常回掉,在多订阅的情况下,为了实现订阅的独立性,将异常的处理放在回掉函数中。
     *
     * @param exception 异常信息
     */
    default void error(Exception exception) {
    }

    ;

}
