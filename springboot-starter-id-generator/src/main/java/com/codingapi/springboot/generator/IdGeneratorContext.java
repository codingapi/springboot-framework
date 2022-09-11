package com.codingapi.springboot.generator;

import com.codingapi.springboot.generator.dao.IdKeyDao;
import com.codingapi.springboot.generator.service.IdGenerateService;

public class IdGeneratorContext {

    private static IdGeneratorContext instance;
    private IdGenerateService idGenerateService;

    private IdGeneratorContext() {
    }

    public static IdGeneratorContext getInstance() {
        if (instance == null) {
            synchronized (IdGeneratorContext.class) {
                if (instance == null) {
                    instance = new IdGeneratorContext();
                }
            }
        }
        return instance;
    }

    long generateId(Class<?> clazz) {
        return idGenerateService.generateId(clazz);
    }

    void init(IdKeyDao idKeyDao) {
        idGenerateService = new IdGenerateService(idKeyDao);
    }

}
