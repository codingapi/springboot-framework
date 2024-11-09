package com.codingapi.springboot.framework.event;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件栈上下文
 */
public class EventStackContext {

    private final Map<String, List<Class<?>>> eventClassStack = new HashMap<>();
    private final Map<String, List<IEvent>> eventStack = new HashMap<>();

    @Getter
    private final static EventStackContext instance = new EventStackContext();

    private EventStackContext() {

    }

    private void addEventClass(String traceId, IEvent event) {
        List<Class<?>> events = eventClassStack.get(traceId);
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event.getClass());
        eventClassStack.put(traceId, events);
    }

    private void addEventStack(String traceId, IEvent event) {
        List<IEvent> events = eventStack.get(traceId);
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
        eventStack.put(traceId, events);
    }


    void addEvent(String traceId, IEvent event) {
        addEventClass(traceId, event);
        addEventStack(traceId, event);
    }

    boolean checkEventLoop(String traceId, IEvent event) {
        List<Class<?>> events = eventClassStack.get(traceId);
        if (events != null) {
            return events.contains(event.getClass());
        }
        return false;
    }

    public List<IEvent> getEvents(String eventKey) {
        if(eventKey!=null) {
            String traceId = eventKey.split("#")[0];
            return eventStack.get(traceId);
        }
        return null;
    }

    public List<Class<?>> getEventClasses(String eventKey) {
        if(eventKey!=null) {
            String traceId = eventKey.split("#")[0];
            return eventClassStack.get(traceId);
        }
        return null;
    }


    void remove(String traceId) {
        eventStack.remove(traceId);
        eventClassStack.remove(traceId);
    }


}
