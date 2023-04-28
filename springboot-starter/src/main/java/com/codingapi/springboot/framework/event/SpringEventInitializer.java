package com.codingapi.springboot.framework.event;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class SpringEventInitializer implements InitializingBean {

    private final ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        DomainEventContext.getInstance().initContext(context);
    }
}
