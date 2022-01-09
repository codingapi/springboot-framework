package com.codingapi.springboot.framework.persistence;

import com.codingapi.springboot.framework.handler.IHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PersistenceHandler implements IHandler<PersistenceEvent> {

    private List<IPersistenceRepository> persistenceList;

    public PersistenceHandler(List<IPersistenceRepository> persistenceList) {
        if(persistenceList==null) {
            this.persistenceList = new ArrayList<>();
        }else{
            this.persistenceList = persistenceList;
        }
    }


    private Class<?> persistenceType(Class<?> persistenceClazz){
        Type[] types = persistenceClazz.getGenericInterfaces();
        for (Type type : types) {
            if(type instanceof Class){
                return persistenceType((Class<?>) type);
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments != null) {
                Class<?> clazz =  (Class<?>) actualTypeArguments[0];
                if(IPersistence.class.isAssignableFrom(clazz)){
                    return clazz;
                }
            }
        }
        return null;
    }


    private void eachPersistence(Object obj){
        for(IPersistenceRepository persistence:persistenceList) {
            Class<?> clazz = persistenceType(persistence.getClass());
            if (clazz!=null&&clazz.equals(obj.getClass())) {
                persistence.persistenceHandler(obj);
            }
        }
    }

    @Override
    public void handler(PersistenceEvent event) {
        Object obj =  event.getVal();
        log.debug("handler persistence event obj:{}",obj);
        this.eachPersistence(obj);
    }


}
