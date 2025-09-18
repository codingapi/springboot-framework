package com.codingapi.springboot.framework.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class EventException extends RuntimeException {

    private final List<Exception> error;

    public EventException(List<Exception> error) {
        super(EventException.convert(error));
        this.error = error;
        for (Exception e : error) {
            this.addSuppressed(e);
        }
    }

    private static String convert(List<Exception> errors){
        if (errors == null || errors.isEmpty()) {
            return "No errors.";
        }
        StringBuilder message = new StringBuilder();
        message.append("Has ").append(errors.size()).append(" Errors:\n");
        int index = 1;
        for (Exception exception : errors) {
            message.append(index++)
                    .append(". ")
                    .append(exception.getClass().getSimpleName())
                    .append(": ")
                    .append(exception.getMessage())
                    .append("\n");
        }
        return message.toString();
    }
}
