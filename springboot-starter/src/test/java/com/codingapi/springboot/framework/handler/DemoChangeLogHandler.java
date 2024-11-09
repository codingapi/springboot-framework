package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.DemoChangeEvent;
import com.codingapi.springboot.framework.event.Handler;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class DemoChangeLogHandler implements IHandler<DemoChangeEvent> {

    @Override
    public void handler(DemoChangeEvent event) {
        log.info("print before name :{},current name :{}", event.getBeforeName(), event.getCurrentName());
    }

    @Override
    public void error(Exception exception) {
        log.error("DemoChangeLogHandler error", exception);
    }
}
