package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IEvent;

import java.util.ArrayList;
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


    @Override
    public void handler(IEvent event) {
        for (IHandler<IEvent> handler : handlers) {
            try {
                Class<?> eventClass = event.getClass();
                Class<?> targetClass = handler.getHandlerEventClass();
                if (eventClass.equals(targetClass)) {
                    handler.handler(event);
                }
            } catch (Exception e) {
                Exception error = null;
                try {
                    handler.error(e);
                } catch (Exception err) {
                    error = err;
                }
                if (error != null) {
                    throw new RuntimeException(error);
                }
            }
        }
    }




}
