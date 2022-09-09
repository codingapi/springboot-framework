package com.codingapi.springboot.example.domain.repository;

import com.codingapi.springboot.example.domain.entity.Demo;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface DemoRepository {
    void save(Demo demo);

}
