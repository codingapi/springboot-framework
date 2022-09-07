package com.codingapi.springboot.example.handler;

import com.codingapi.springboot.example.event.DemoNameChangeEvent;
import com.codingapi.springboot.framework.handler.Handler;
import com.codingapi.springboot.framework.handler.IHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
@Handler
public class DemoNameMsgHandler implements IHandler<DemoNameChangeEvent> {

    @Override
    public void handler(DemoNameChangeEvent event) {
        log.info("oldName:{},currentName:{}", event.getOldName(), event.getCurrentName());
    }

}
