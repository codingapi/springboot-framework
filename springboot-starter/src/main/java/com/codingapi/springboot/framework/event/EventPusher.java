package com.codingapi.springboot.framework.event;

import java.util.Arrays;
import java.util.List;

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

    /**
     * 批量推送：整批事件共享同一 trace，异步事件将严格按参数顺序串行执行。
     * 批内同类事件不会被误判为循环调用。
     *
     * @param events 事件（按期望的执行顺序排列）
     */
    public static void push(IEvent... events) {
        if (events == null || events.length == 0) {
            return;
        }
        DomainEventContext.getInstance().pushBatch(Arrays.asList(events));
    }
}
