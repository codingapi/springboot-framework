package com.codingapi.springboot.script.bind;

import java.util.ArrayList;
import java.util.List;

public class ObjectBinderBuilder {

    private final List<ObjectBinder> list;

    private ObjectBinderBuilder() {
        this.list = new ArrayList<>();
    }

    public static ObjectBinderBuilder builder(){
        return new ObjectBinderBuilder();
    }

    public ObjectBinderBuilder add(String name, Object value) {
        this.list.add(new ObjectBinder(name, value));
        return this;
    }


    public List<ObjectBinder> build() {
        return this.list;
    }
}
