package com.codingapi.springboot.framework.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EventException extends RuntimeException {

    private final List<Exception> error;

    public EventException(List<Exception> error) {
        super(error.stream().map(Exception::getMessage).collect(Collectors.joining("\n")));
        this.error = error;
    }
}
