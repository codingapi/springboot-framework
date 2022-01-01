package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class ApplicationHandlerUtils implements IHandler<IEvent>{

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

    public void addHandlers(List<IHandler> handleres){
        if(handleres!=null){
            handleres.forEach(this::addHandler);
        }
    }


    public void addHandler(IHandler handler){
        if(handler!=null){
            handleres.add(handler);
        }
    }


    @Override
    public void handler(IEvent event) {
        for(IHandler<IEvent> handler:handleres){
            try{
                Class<?> eventClass = event.getClass();
                Class<?> targetClass =  getHandlerEventClass(handler);
                if(eventClass.equals(targetClass)) {
                    handler.handler(event);
                }
            }catch(Exception e){
                log.warn("handler exception", e);
                handler.error(e);
            }
        }    
    }

    private Class<?> getHandlerEventClass(IHandler<IEvent> handler){
        Type[] types = handler.getClass().getGenericInterfaces();
        for(Type type:types){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments =  parameterizedType.getActualTypeArguments();
            if(actualTypeArguments!=null){
                return (Class<?>) actualTypeArguments[0];
            }

        }
        return null;
    }

    
}
