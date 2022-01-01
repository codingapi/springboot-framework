package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.ApplicationDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.util.List;

@Slf4j
public class SpringEventHandler implements ApplicationListener<ApplicationDomainEvent>{


    public SpringEventHandler(List<IHandler> handlers){
        ApplicationHandlerUtils.getInstance().addHandlers(handlers);
    }

    @Override
    public void onApplicationEvent(ApplicationDomainEvent event){
        ApplicationHandlerUtils.getInstance().handler(event.getEvent());    
    }
    
}
