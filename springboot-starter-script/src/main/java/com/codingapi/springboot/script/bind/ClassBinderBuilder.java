package com.codingapi.springboot.script.bind;

import java.util.ArrayList;
import java.util.List;

public class ClassBinderBuilder {

    private final List<ClassBinder> list;

    private ClassBinderBuilder() {
        this.list = new ArrayList<>();
    }

    public static ClassBinderBuilder builder(){
        return new ClassBinderBuilder();
    }

    public ClassBinderBuilder add(String name, Class<?> clazz) {
        this.list.add(new ClassBinder(name, clazz));
        return this;
    }


    public List<ClassBinder> build() {
        return this.list;
    }
}
