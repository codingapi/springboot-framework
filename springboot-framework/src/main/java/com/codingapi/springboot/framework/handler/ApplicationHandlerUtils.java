package com.codingapi.springboot.framework.handler;

import java.util.ArrayList;
import java.util.List;

import com.codingapi.springboot.framework.event.IEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationHandlerUtils implements IHandler<IEvent>{

    private static ApplicationHandlerUtils instance;


    private ApplicationHandlerUtils(){
        this.handleres = new ArrayList<>();
    }


    public static ApplicationHandlerUtils getInstance(){
        if(instance==null){
            synchronized(ApplicationHandlerUtils.class){
                if(instance ==null ){
                    instance = new ApplicationHandlerUtils();
                }
            }
        }
        return instance;
    }


    private List<IHandler<IEvent>> handleres;

    public void addHandlers(List<IHandler<IEvent>> handleres){
        if(handleres!=null){
            handleres.addAll(handleres);
        }
    }


    public void addHandler(IHandler<IEvent> handler){
        if(handler!=null){
            handleres.add(handler);
        }
    }


    @Override
    public void handler(IEvent event) {
        for(IHandler<IEvent> handler:handleres){
            try{
                handler.handler(event);
            }catch(Exception e){
                log.error("handler exception", e);
            }
        }    
    }

    
}
