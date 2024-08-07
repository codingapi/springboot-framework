package com.codingapi.springboot.fast.jpa.repository;

import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T,ID> extends JpaRepository<T,ID> {

    @SuppressWarnings("unchecked")
    default Class<T> getEntityClass() {
        ResolvableType resolvableType = ResolvableType.forClass(getClass()).as(BaseRepository.class);
        return (Class<T>) resolvableType.getGeneric(0).resolve();
    }

}
