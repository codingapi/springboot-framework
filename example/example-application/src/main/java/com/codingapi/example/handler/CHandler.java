package com.codingapi.example.handler;

import com.codingapi.example.event.AEvent;
import com.codingapi.example.event.CEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.event.EventTraceContext;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CHandler implements IHandler<CEvent> {

    @Override
    public void handler(CEvent event) {
        log.info("c event:{},eventKey:{}", event, EventTraceContext.getInstance().getEventKey());

//        EventPusher.push(new AEvent(),true);
//        throw new RuntimeException("c handler error");
    }

    @Override
    public void error(Exception exception) throws Exception {
        log.error("c handler error:{}", exception.getMessage());
        throw exception;
    }
}
