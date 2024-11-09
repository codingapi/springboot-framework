package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.domain.event.DomainCreateEvent;
import com.codingapi.springboot.framework.event.Handler;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class DemoCreateHandler implements IHandler<DomainCreateEvent> {

    @Override
    public void handler(DomainCreateEvent event) {
        log.info("create domain -> {}", event.getEntity());
    }

}
