package com.codingapi.springboot.script.request;

import java.util.ArrayList;
import java.util.List;

public class GroovyBindObjectBuilder {

    private final List<GroovyBindObject> list;

    private GroovyBindObjectBuilder() {
        this.list = new ArrayList<>();
    }

    public static GroovyBindObjectBuilder builder(){
        return new GroovyBindObjectBuilder();
    }

    public GroovyBindObjectBuilder add(String name, Object value) {
        this.list.add(new GroovyBindObject(name, value));
        return this;
    }


    public List<GroovyBindObject> build() {
        return this.list;
    }
}
