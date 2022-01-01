package com.codingapi.springboot.example.domain.handler;

import com.codingapi.springboot.example.domain.event.DemoNameChangeEvent;
import com.codingapi.springboot.framework.handler.Handler;
import com.codingapi.springboot.framework.handler.IHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
@Handler
public class DemoNameLogHandler implements IHandler<DemoNameChangeEvent> {

    @Override
    public void handler(DemoNameChangeEvent event) {
      log.info("oldName:{},currentName:{}",event.getOldName(),event.getCurrentName());
    }

}
