package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IHandler;
import com.codingapi.springboot.framework.event.MyTestSyncEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTestSyncHandler implements IHandler<MyTestSyncEvent> {

    @Override
    public void handler(MyTestSyncEvent event) {
        log.info("event:{}",event.getMyTest());
    }
}
