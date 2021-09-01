package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IEvent;

public interface IHandler {

    void handler(IEvent event);
    
}
