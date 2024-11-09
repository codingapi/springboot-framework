package com.codingapi.example.handler;

import com.codingapi.example.event.BEvent;
import com.codingapi.example.event.CEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.event.EventTraceContext;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BHandler implements IHandler<BEvent> {

    @Override
    public void handler(BEvent event) {
        log.info("b event:{},eventKey:{}",event, EventTraceContext.getInstance().getEventKey());

        EventPusher.push(new CEvent());
    }
}
