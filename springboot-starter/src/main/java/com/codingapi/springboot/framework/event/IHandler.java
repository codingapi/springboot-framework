package com.codingapi.springboot.framework.event;

/**
 * handler 订阅
 *
 * @param <T> Event 类型
 */
public interface IHandler<T extends IEvent> {

    /**
     * 事件订阅排序
     * 在同样的事件中，可以通过order来控制订阅的顺序
     */
    default int order() {
        return 0;
    }

    /**
     * 订阅触发
     *
     * @param event 触发event
     */
    void handler(T event);

    /**
     * 异常回掉,在多订阅的情况下,为了实现订阅的独立性,将异常的处理放在回掉函数中。
     * 当异常抛出以后，会阻止后续的事件执行
     *
     * @param exception 异常信息
     */
    default void error(Exception exception) throws Exception {
        throw exception;
    }




}
