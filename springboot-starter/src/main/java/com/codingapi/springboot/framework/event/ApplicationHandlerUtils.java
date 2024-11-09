package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventException;
import com.codingapi.springboot.framework.exception.EventLoopException;
import org.springframework.core.ResolvableType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ApplicationHandlerUtils implements IHandler<IEvent> {

    private static ApplicationHandlerUtils instance;
    private final List<IHandler<IEvent>> handlers;


    private ApplicationHandlerUtils() {
        this.handlers = new ArrayList<>();
    }

    public static ApplicationHandlerUtils getInstance() {
        if (instance == null) {
            synchronized (ApplicationHandlerUtils.class) {
                if (instance == null) {
                    instance = new ApplicationHandlerUtils();
                }
            }
        }
        return instance;
    }

    public void addHandlers(List<IHandler> handlers) {
        if (handlers != null) {
            handlers.forEach(this::addHandler);
        }
    }


    public void addHandler(IHandler handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    /**
     * 获取订阅的事件类型
     */
    private Class<?> getHandlerEventClass(IHandler<?> handler) {
        ResolvableType resolvableType = ResolvableType.forClass(handler.getClass()).as(IHandler.class);
        return resolvableType.getGeneric(0).resolve();
    }


    @Override
    public void handler(IEvent event) {
        Class<?> eventClass = event.getClass();

        List<IHandler<IEvent>> matchHandlers = handlers
                .stream()
                .filter(handler -> {
                    Class<?> targetClass = getHandlerEventClass(handler);
                    return targetClass.isAssignableFrom(eventClass);
                })
                .sorted(Comparator.comparingInt(IHandler::order))
                .toList();

        if (matchHandlers.isEmpty()) {
            return;
        }

        List<Exception> errorStack = new ArrayList<>();
        boolean hasThrowException = false;
        for (IHandler<IEvent> handler : matchHandlers) {
            try {
                handler.handler(event);
            } catch (Exception e) {
                if (e instanceof EventLoopException) {
                    throw e;
                }
                try {
                    handler.error(e);
                    errorStack.add(e);
                } catch (Exception err) {
                    hasThrowException = true;
                    errorStack.add(err);
                }
            }
        }
        if (hasThrowException) {
            throw new EventException(errorStack);
        }
    }


}
