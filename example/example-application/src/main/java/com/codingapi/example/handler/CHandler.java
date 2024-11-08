package com.codingapi.example.handler;

import com.codingapi.example.event.AEvent;
import com.codingapi.example.event.CEvent;
import com.codingapi.springboot.framework.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CHandler implements IHandler<CEvent> {

    @Override
    public void handler(CEvent event) {
        String eventKey = EventTraceContext.getInstance().getEventKey();
        log.info("c event:{},eventKey:{}",event, EventTraceContext.getInstance().getEventKey());
        List<IEvent> eventList =  EventLogContext.getInstance().getEvents(eventKey);
        log.info("events:{}",eventList);

        EventPusher.push(new AEvent());
    }
}
