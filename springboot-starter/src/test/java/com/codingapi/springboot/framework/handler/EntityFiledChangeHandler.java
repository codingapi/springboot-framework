package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.domain.event.DomainChangeEvent;
import com.codingapi.springboot.framework.event.Handler;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class EntityFiledChangeHandler implements IHandler<DomainChangeEvent> {

    @Override
    public void handler(DomainChangeEvent event) {
        log.info("field change event -> {}",event);
    }
}
