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
        log.info("c event:{},traceId:{}",event, EventTraceContext.getInstance().getListenerKey());

        EventPusher.push(new AEvent());
    }
}
