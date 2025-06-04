package com.codingapi.example.domain.user.event;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;

public class UserEvent implements IEvent {

    @Getter
    private final User user;

    public UserEvent(User user) {
        this.user = user;
    }
}
