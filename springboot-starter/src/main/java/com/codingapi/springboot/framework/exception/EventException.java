package com.codingapi.springboot.framework.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EventException extends RuntimeException {

    private final List<Exception> errors;

    public EventException(List<Exception> errors) {
        super(buildMessage(errors), errors.isEmpty() ? null : errors.get(0)); // 第一个异常作为 cause
        this.errors = errors;

        // 把其他异常挂在 suppressed 上
        for (int i = 1; i < errors.size(); i++) {
            this.addSuppressed(errors.get(i));
        }
    }

    private static String buildMessage(List<Exception> errors) {
        return errors.stream()
                .map(e -> e.getClass().getSimpleName() + ": " + e.getMessage())
                .collect(Collectors.joining("; "));
    }
}
