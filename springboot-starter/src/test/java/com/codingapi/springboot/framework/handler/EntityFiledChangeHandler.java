package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.domain.event.FieldChangeEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class EntityFiledChangeHandler implements IHandler<FieldChangeEvent>{

    @Override
    public void handler(FieldChangeEvent event) {
        log.info("field change event -> {}",event);
    }
}
