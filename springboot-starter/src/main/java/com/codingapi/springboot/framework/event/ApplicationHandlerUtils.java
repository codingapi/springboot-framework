package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventException;
import com.codingapi.springboot.framework.exception.EventLoopException;

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
        Class<?> eventClass = event.getClass();
        List<Exception> errorStack = new ArrayList<>();
        boolean throwException = false;
        for (IHandler<IEvent> handler : handlers) {
            try {
                Class<?> targetClass = handler.getHandlerEventClass();
                if (eventClass.equals(targetClass)) {
                    handler.handler(event);
                }
            } catch (Exception e) {
                if (e instanceof EventLoopException) {
                    throw e;
                }
                try {
                    handler.error(e);
                    errorStack.add(e);
                } catch (Exception err) {
                    throwException = true;
                    errorStack.add(err);
                }
            }
        }
        if(throwException){
            throw new EventException(errorStack);
        }
    }


}
