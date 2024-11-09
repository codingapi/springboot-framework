package com.codingapi.springboot.framework.event;

/**
 * Even推送助手
 */
public class EventPusher {

    /**
     *  推送事件
     *  默认将自动检测事件是否有循环事件，当出现循环事件时，系统将会抛出循环调用异常。
     * @param event 事件
     */
    public static void push(IEvent event) {
        push(event, false);
    }

    /**
     * 推送事件
     * 默认将自动检测事件是否有循环事件，当出现循环事件时，系统将会抛出循环调用异常。
     * 设置hasLoopEvent为true，将不会检测循环事件。
     * @param event 事件
     * @param hasLoopEvent 是否有循环事件
     */
    public static void push(IEvent event, boolean hasLoopEvent) {
        DomainEventContext.getInstance().push(event, hasLoopEvent);
    }
}
