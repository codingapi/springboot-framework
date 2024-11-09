package com.codingapi.example.handler;

import com.codingapi.springboot.framework.event.IEvent;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GlobalEventHandler implements IHandler<IEvent> {

    @Override
    public void handler(IEvent event) {
        log.info("global event:{}", event);
    }
}
