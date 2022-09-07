package com.codingapi.springboot.framework.event;

/**
 * Even推送助手
 */
public class EventPusher {

    public static void push(IEvent event) {
        DomainEventContext.getInstance().push(event);
    }
}
