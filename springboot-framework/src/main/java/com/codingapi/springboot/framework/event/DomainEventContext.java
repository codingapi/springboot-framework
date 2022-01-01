package com.codingapi.springboot.framework.event;

import org.springframework.context.ApplicationContext;

public class DomainEventContext {

    private static DomainEventContext instance;

    public static DomainEventContext getInstance(){
        if(instance==null){
            synchronized(DomainEventContext.class){
                if(instance==null){
                    instance = new DomainEventContext();
                }
            }
        }
        return instance;
    }
    

    private DomainEventContext(){

    }

    private ApplicationContext context;


    private void push(IEvent event,boolean sync){
        if(context!=null){
            context.publishEvent(new DomainEvent(event,sync));
        }        
    }

    /**
     * @see EventPusher
     * 默认 同步事件
     * @param event
     *
     */
    @Deprecated
    public void push(IEvent event){
        this.syncPush(event);
    }

    /**
     * @see EventPusher
     * 同步事件
     * @param event
     */
    @Deprecated
    public void syncPush(IEvent event){
        this.push(event,true);
    }

    /**
     * @see EventPusher
     * 异步事件
     * @param event
     */
    @Deprecated
    public void asyncPush(IEvent event){
        this.push(event,false);
    }

    protected void initContext(ApplicationContext context){
        this.context = context;
    }


    
}
