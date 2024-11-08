package com.codingapi.example.handler;

import com.codingapi.example.event.AEvent;
import com.codingapi.example.event.BEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.event.EventTraceContext;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AHandler implements IHandler<AEvent> {

    @Override
    public void handler(AEvent event) {
        log.info("a event:{},traceId:{}",event, EventTraceContext.getInstance().getListenerKey());

        EventPusher.push(new BEvent());
    }
}
