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
        if(event instanceof IAsyncEvent) {
            this.push(event, false);
        }else if(event instanceof ISyncEvent) {
            this.push(event, true);
        }else{
            this.push(event, true);
        }
    }


    protected void initContext(ApplicationContext context){
        this.context = context;
    }


    
}
