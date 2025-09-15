package com.codingapi.springboot.framework.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EventException extends RuntimeException {

    private final List<Exception> error;

    public EventException(List<Exception> error) {
        super(buildMessage(error), error.isEmpty() ? null : error.get(0)); // 第一个异常作为 cause
        this.error = error;

        // 把其他异常挂在 suppressed 上
        for (int i = 1; i < error.size(); i++) {
            this.addSuppressed(error.get(i));
        }
    }
    private static String buildMessage(List<Exception> errors) {
        return errors.stream()
                .map(e -> e.getClass().getSimpleName() + ": " + e.getMessage())
                .collect(Collectors.joining("; "));
    }
}
