package com.codingapi.springboot.framework.event;

import org.springframework.context.ApplicationContext;

public class ApplicationEventUtils {

    private static ApplicationEventUtils instance;

    public static ApplicationEventUtils getInstance(){
        if(instance==null){
            synchronized(ApplicationEventUtils.class){
                if(instance==null){
                    instance = new ApplicationEventUtils();
                }
            }
        }
        return instance;
    }
    

    private ApplicationEventUtils(){

    }

    private ApplicationContext context;


    public void push(IEvent event){
        if(context!=null){
            context.publishEvent(new ApplicationDomainEvent(event));
        }        
    }


    protected void initContext(ApplicationContext context){
        this.context = context;
    }


    
}
