package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.domain.event.DomainDeleteEvent;
import com.codingapi.springboot.framework.event.Handler;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class DemoDeleteHandler implements IHandler<DomainDeleteEvent> {

    @Override
    public void handler(DomainDeleteEvent event) {
        log.info("delete domain -> {}", event.getEntity());
    }

}
