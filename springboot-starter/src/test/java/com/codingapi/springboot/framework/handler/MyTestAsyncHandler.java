package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IHandler;
import com.codingapi.springboot.framework.event.MyTestAsyncEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTestAsyncHandler implements IHandler<MyTestAsyncEvent> {

    @Override
    public void handler(MyTestAsyncEvent event) {
        log.info("event:{}",event.getMyTest());
    }
}
