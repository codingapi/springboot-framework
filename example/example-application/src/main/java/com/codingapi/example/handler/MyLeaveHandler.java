package com.codingapi.example.handler;

import com.codingapi.example.event.UserEvent;
import com.codingapi.springboot.framework.event.IHandler;
import org.springframework.stereotype.Component;

@Component
public class MyLeaveHandler implements IHandler<UserEvent> {

    @Override
    public void handler(UserEvent event) {

    }
}
