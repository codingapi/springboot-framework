package com.codingapi.springboot.example.domain.handler;

import com.codingapi.springboot.example.domain.event.DemoNameChangeEvent;
import com.codingapi.springboot.framework.handler.BaseHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class DemoNameLogHandler extends BaseHandler<DemoNameChangeEvent> {

    @Override
    public void handler0(DemoNameChangeEvent event) {
      log.info("oldName:{},currentName:{}",event.getOldName(),event.getCurrentName());
    }

}
