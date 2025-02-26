package com.codingapi.example.event;

import com.codingapi.example.domain.User;
import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;


public class UserEvent implements IEvent {

    @Getter
    private final User user;

    public UserEvent(User user) {
        this.user = user;
    }
}
