package com.codingapi.springboot.framework.event;

public class EventPusher {

    public static void push(IEvent event){
        DomainEventContext.getInstance().push(event);
    }
}
