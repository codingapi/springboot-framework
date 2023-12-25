package com.codingapi.springboot.persistence;

import java.util.List;

public interface DomainPersistence {

    void save(Object domain);

    <T> T get(Class<T> domainClass, Object id);

    void delete(Class<?> domainClass,Object id);

    void update(Object domain);

    <T> List<T> find(Class<T> domainClass, String schema, Object... fields);
}
