package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventLoopException;
import com.codingapi.springboot.framework.utils.RandomGenerator;
import lombok.Getter;

import java.util.*;

public class EventTraceContext {

    @Getter
    private final static EventTraceContext instance = new EventTraceContext();

    // trace key
    private final Set<String> traceKeys = new HashSet<>();

    // thread local
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // event listener state
    private final Map<String, Boolean> eventKeyState = new HashMap<>();

    // event stack
    private final Map<String, List<Class<?>>> eventStack = new HashMap<>();

    private EventTraceContext() {
    }

    String getOrCreateTrace() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            return eventKey.split("#")[0];
        }
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        traceKeys.add(traceId);
        return traceId;
    }

    public String getEventKey(){
        return threadLocal.get();
    }

    void createEventKey(String traceId) {
        String eventKey = traceId + "#" + RandomGenerator.randomString(8);
        eventKeyState.put(eventKey, false);
        threadLocal.set(eventKey);
    }

    void checkEventState() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            boolean state = eventKeyState.get(eventKey);
            if (!state) {
                // event execute finish
                String traceId = eventKey.split("#")[0];
                traceKeys.remove(traceId);
                eventStack.remove(traceId);
                EventLogContext.getInstance().removeEvents(traceId);
            }
        }
        eventKeyState.remove(eventKey);
        threadLocal.remove();
    }

    void addEvent(String traceId, IEvent event) {
        List<Class<?>> stack = eventStack.get(traceId);
        if (stack == null) {
            stack = new ArrayList<>();
        } else {
            if (stack.contains(event.getClass())) {
                //清空trace记录
                traceKeys.remove(traceId);
                eventStack.remove(traceId);
                eventKeyState.remove(traceId);
                threadLocal.remove();
                EventLogContext.getInstance().removeEvents(traceId);
                throw new EventLoopException(stack, event);
            }
        }
        EventLogContext.getInstance().addEvent(traceId,event);
        stack.add(event.getClass());
        eventStack.put(traceId, stack);
    }
}
