package com.codingapi.springboot.generator;

public interface IdGenerate {

    default long generateLongId() {
        return IdGeneratorContext.getInstance().generateId(getClass());
    }

    default int generateIntId() {
        return (int) IdGeneratorContext.getInstance().generateId(getClass());
    }

    default String generateStringId() {
        return String.valueOf(IdGeneratorContext.getInstance().generateId(getClass()));
    }

}
