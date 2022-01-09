package com.codingapi.springboot.example.repository;

import com.codingapi.springboot.example.domain.Demo;
import com.codingapi.springboot.framework.persistence.IPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface DemoRepository extends JpaRepository<Demo,Integer>, IPersistenceRepository<Demo> {

    @Override
    default void persistenceHandler(Demo demo) {
        save(demo);
    }
}
