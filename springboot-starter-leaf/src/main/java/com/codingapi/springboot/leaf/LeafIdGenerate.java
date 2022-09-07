package com.codingapi.springboot.leaf;

public interface LeafIdGenerate {


    default long generateLongId() {
        return LeafContext.getInstance().generateId(getClass());
    }

    default int generateIntId() {
        return (int) LeafContext.getInstance().generateId(getClass());
    }

    default String generateStringId() {
        return String.valueOf(LeafContext.getInstance().generateId(getClass()));
    }

}
