package com.codingapi.springboot.framework.event;

public class EventPusher {

    public static void push(IEvent event){
        DomainEventContext.getInstance().push(event);
    }

    public static void asyncPush(IEvent event){
        DomainEventContext.getInstance().asyncPush(event);
    }

    public static void syncPush(IEvent event){
        DomainEventContext.getInstance().syncPush(event);
    }
}
