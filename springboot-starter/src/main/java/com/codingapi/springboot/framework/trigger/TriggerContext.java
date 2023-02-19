package com.codingapi.springboot.framework.trigger;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Trigger与Event模式都提供了订阅的功能。
 * Trigger与Event差异是,Event是消息驱动性，而Trigger是订阅驱动性。
 * 两者的差异在于，Event是确定了消息而不确定订阅方，而Trigger则是确定了订阅再等待消息触发。
 *
 * Trigger模式可以控制触发的规则,例如是否进入触发器,触发器是否在触发以后删除。
 */
@SuppressWarnings("all")
@Slf4j
public class TriggerContext{

    public static TriggerContext getInstance() {
        return instance;
    }

    private final static TriggerContext instance = new TriggerContext();


    private final Map<Class<? extends Trigger>,List<TriggerHandler>> triggers;

    private TriggerContext(){
        this.triggers = new ConcurrentHashMap<>();
    }

    /**
     * 添加触发器
     * @param handler 触发订阅
     */
    public void addTrigger(TriggerHandler handler){
        Class<? extends Trigger> clazz = getTriggerClass(handler.getClass());
        List<TriggerHandler> triggerList =  this.triggers.get(clazz);
        if(triggerList==null){
            triggerList = new CopyOnWriteArrayList<>();
            this.triggers.put(clazz,triggerList);
        }
        triggerList.add(handler);
    }


    /**
     * 获取触发器订阅的Trigger类型
     * @param handler 触发订阅
     * @return Trigger类型
     */
    private Class<? extends Trigger> getTriggerClass(Class<?> handler){
        for(Class<?> superInterface : handler.getInterfaces()) {
            if (superInterface.equals(TriggerHandler.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) handler.getGenericInterfaces()[0];
                return (Class<? extends Trigger>) parameterizedType.getActualTypeArguments()[0];
            }
        }
        return getTriggerClass(handler.getSuperclass());
    }


    /**
     * 执行触发
     * @param trigger trigger触发
     */
    public void trigger(Trigger trigger){
        Class<? extends Trigger> clazz = trigger.getClass();
        List<TriggerHandler> triggerHandlerList = triggers.get(clazz);
        for(TriggerHandler handler:triggerHandlerList){
            Class<? extends Trigger> triggerClass = getTriggerClass(handler.getClass());
            if(triggerClass.equals(clazz)) {
                try {
                    boolean canTrigger = handler.preTrigger(trigger);
                    if (canTrigger) {
                        handler.trigger(trigger);
                    }
                    if (handler.remove(trigger,canTrigger)) {
                        triggerHandlerList.remove(handler);
                    }
                }catch (Exception e){
                    log.warn("trigger error",e);
                }
            }
        }
    }

    /**
     * 清空触发
     * @param clazz 清空的trigger类型
     */
    public void clear(Class<? extends Trigger> clazz){
        triggers.get(clazz).clear();
    }


    /**
     * 判断是否清空的对象
     * @param clazz 清空的trigger类型
     * @return true为空
     */
    public boolean isEmpty(Class<? extends Trigger> clazz){
        return triggers.get(clazz).isEmpty();
    }

}
