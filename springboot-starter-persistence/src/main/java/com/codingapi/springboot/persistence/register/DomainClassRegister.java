package com.codingapi.springboot.persistence.register;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DomainClassRegister {

    private final List<Class<?>> classes;

    private final static DomainClassRegister INSTANCE = new DomainClassRegister();

    private DomainClassRegister() {
        this.classes = new ArrayList<>();
    }

    public DomainClassRegister register(Class<?> domainClass) {
        this.classes.add(domainClass);
        return this;
    }

    public void register(Class<?>... domainClasses) {
        this.classes.addAll(List.of(domainClasses));
    }

    public boolean supports(Class<?> domainClass) {
        return this.classes.stream()
                .anyMatch(clazz -> clazz.equals(domainClass));

    }

    public static DomainClassRegister getInstance() {
        return INSTANCE;
    }

}