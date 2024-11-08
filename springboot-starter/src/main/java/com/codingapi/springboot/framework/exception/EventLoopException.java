package com.codingapi.springboot.framework.exception;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class EventLoopException extends RuntimeException {

    private final List<Class<?>> stack;

    public EventLoopException(List<Class<?>> stack, IEvent event) {
        super("event loop error current event class:" + event.getClass() + ", history event stack:" + stack);
        this.stack = stack;
    }
}
