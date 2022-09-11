package com.codingapi.springboot.generator;

public interface IdGenerate {

    default long generateLongId() {
        return GeneratorContext.getInstance().generateId(getClass());
    }

    default int generateIntId() {
        return (int) GeneratorContext.getInstance().generateId(getClass());
    }

    default String generateStringId() {
        return String.valueOf(GeneratorContext.getInstance().generateId(getClass()));
    }

}
