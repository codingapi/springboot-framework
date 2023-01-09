package com.codingapi.springboot.framework.trigger;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class TriggerContext{

    public static TriggerContext getInstance() {
        return instance;
    }

    private final static TriggerContext instance = new TriggerContext();


    private final Map<Class<? extends Trigger>,List<TriggerHandler>> triggers;

    private TriggerContext(){
        this.triggers = new ConcurrentHashMap<>();
    }

    public void addTrigger(TriggerHandler trigger){
        Class<? extends Trigger> clazz = getTriggerClass(trigger);
        List<TriggerHandler> triggerList =  this.triggers.get(clazz);
        if(triggerList==null){
            triggerList = new ArrayList<>();
            this.triggers.put(clazz,triggerList);
        }
        triggerList.add(trigger);
    }


    private Class<? extends Trigger> getTriggerClass(TriggerHandler trigger){
        ParameterizedType parameterizedType = (ParameterizedType) trigger.getClass().getGenericInterfaces()[0];
        return (Class<? extends Trigger>) parameterizedType.getActualTypeArguments()[0];
    }


    public void trigger(Trigger trigger){
        Class<? extends Trigger> clazz = trigger.getClass();
        Iterator<TriggerHandler> iterator = triggers.get(clazz).iterator();
        while (iterator.hasNext()){
            TriggerHandler handler = iterator.next();
            Class<? extends Trigger> triggerClass = getTriggerClass(handler);
            if(triggerClass.equals(clazz)) {
                if (handler.preTrigger(trigger)) {
                    handler.trigger(trigger);
                    if (handler.remove()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void clear(Class<? extends Trigger> clazz){
        triggers.get(clazz).clear();
    }


    public boolean isEmpty(Class<? extends Trigger> clazz){
        return triggers.get(clazz).isEmpty();
    }

}
