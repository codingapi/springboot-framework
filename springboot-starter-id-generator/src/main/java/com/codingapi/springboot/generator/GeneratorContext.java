package com.codingapi.springboot.generator;

import com.codingapi.springboot.generator.dao.IdKeyDao;

public class GeneratorContext {

    private static GeneratorContext instance;
    private IdGenerateContext idGenerateContext;

    private GeneratorContext() {
    }

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

    long generateId(Class<?> clazz) {
        return idGenerateContext.generateId(clazz);
    }

    void init(IdKeyDao idKeyDao) {
        idGenerateContext = new IdGenerateContext(idKeyDao);
    }

}
