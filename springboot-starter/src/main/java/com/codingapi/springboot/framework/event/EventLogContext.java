package com.codingapi.springboot.framework.event;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件日志记录
 */
public class EventLogContext {


    private final Map<String, List<IEvent>> eventLogs = new HashMap<>();

    @Getter
    private static final EventLogContext instance = new EventLogContext();

    private EventLogContext() {
    }

    void addEvent(String traceId, IEvent event) {
        List<IEvent> eventList = eventLogs.get(traceId);
        if (eventList == null) {
            eventList = new ArrayList<>();
        }
        eventList.add(event);
        eventLogs.put(traceId, eventList);
    }


    void removeEvents(String traceId) {
        eventLogs.remove(traceId);
    }


    public List<IEvent> getEvents(String listenerKey) {
        if (listenerKey != null) {
            String traceId = listenerKey.split("#")[0];
            return eventLogs.get(traceId);
        }
        return null;
    }
}
