package com.codingapi.springboot.persistence.register;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DomainClassRegister {

    private final List<Class<?>> classes;

    public final static DomainClassRegister INSTANCE = new DomainClassRegister();

    private DomainClassRegister() {
        this.classes = new ArrayList<>();
    }

    public void register(Class<?> domainClass) {
        this.classes.add(domainClass);
    }

    public boolean supports(Class<?> domainClass) {
        return this.classes.stream()
                .anyMatch(clazz -> clazz.equals(domainClass));

    }

}
