package com.codingapi.springboot.framework.event;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class SpringEventInitializer {

    private final ApplicationContext context;

    public void init() {
        DomainEventContext.getInstance().initContext(context);
    }

}
