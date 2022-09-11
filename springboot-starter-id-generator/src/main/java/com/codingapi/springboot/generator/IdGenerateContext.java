package com.codingapi.springboot.generator;

import com.codingapi.springboot.generator.dao.IdKeyDao;
import com.codingapi.springboot.generator.domain.IdKey;

public class IdGenerateContext {

    private final IdKeyDao keyDao;

    public IdGenerateContext(IdKeyDao keyDao) {
        this.keyDao = keyDao;
    }

    public synchronized long generateId(Class<?> clazz)  {
        IdKey idKey =  keyDao.getByKey(clazz.getName());
        if(idKey==null){
            idKey = new IdKey(clazz.getName());
            keyDao.save(idKey);
        }else{
            keyDao.updateMaxId(idKey);
        }
        return idKey.getId();
    }
}
