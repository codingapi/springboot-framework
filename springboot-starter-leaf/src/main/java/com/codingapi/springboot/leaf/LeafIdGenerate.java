package com.codingapi.springboot.leaf;

public interface LeafIdGenerate {


    default long generateLongId(){
        return LeafUtils.getInstance().generateId(getClass());
    }

    default int generateIntId(){
        return (int)LeafUtils.getInstance().generateId(getClass());
    }

    default String generateStringId(){
        return String.valueOf(LeafUtils.getInstance().generateId(getClass()));
    }

}
