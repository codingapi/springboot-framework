package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.domain.event.DomainPersistEvent;
import com.codingapi.springboot.framework.event.Handler;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class DemoPersistEventHandler implements IHandler<DomainPersistEvent> {

    @Override
    public void handler(DomainPersistEvent event) {
        log.info("DomainPersistEvent handler {}",event);
    }
}
