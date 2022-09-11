package com.codingapi.springboot.generator;

import com.codingapi.springboot.generator.dao.IdKeyDao;

public class GeneratorContext {

    private GeneratorContext() {
    }

    private static GeneratorContext instance;

    public static GeneratorContext getInstance() {
        if (instance == null) {
            synchronized (GeneratorContext.class) {
                if (instance == null) {
                    instance = new GeneratorContext();
                }
            }
        }
        return instance;
    }

    private IdGenerateContext idGenerateContext;

    long generateId(Class<?> clazz) {
        return idGenerateContext.generateId(clazz);
    }

    void init(IdKeyDao idKeyDao){
        idGenerateContext = new IdGenerateContext(idKeyDao);
    }

}
