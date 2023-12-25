package com.codingapi.springboot.persistence;

public interface DomainPersistence {

    void save(Object domain);

    <T> T get(Class<T> domainClass, Object id);

}
