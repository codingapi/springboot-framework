package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class ApplicationHandlerUtils implements IHandler<IEvent> {

    private static ApplicationHandlerUtils instance;
    private List<IHandler<IEvent>> handlers;


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
            String targetClassName = null;
            try {
                Class<?> eventClass = event.getClass();
                Class<?> targetClass = getHandlerEventClass(handler);
                if (eventClass.equals(targetClass)) {
                    targetClassName = targetClass.getName();
                    handler.handler(event);
                }
            } catch (Exception e) {
                //IPersistenceEvent 抛出异常
                if ("com.codingapi.springboot.framework.persistence.PersistenceEvent".equals(targetClassName)) {
                    throw e;
                }
                log.warn("handler exception", e);
                handler.error(e);

            }
        }
    }

    private Class<?> getHandlerEventClass(IHandler<IEvent> handler) {
        Type[] types = handler.getClass().getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
        }
        return null;
    }


}
