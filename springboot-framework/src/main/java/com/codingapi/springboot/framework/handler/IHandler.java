package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IEvent;

public interface IHandler<T extends IEvent> {

    void handler(T event);

    default void error(Exception exception){};
    
}
