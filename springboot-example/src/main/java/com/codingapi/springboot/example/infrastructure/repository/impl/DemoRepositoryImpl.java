package com.codingapi.springboot.example.infrastructure.repository.impl;

import com.codingapi.springboot.example.domain.entity.Demo;
import com.codingapi.springboot.example.domain.repository.DemoRepository;
import com.codingapi.springboot.example.infrastructure.entity.DemoEntity;
import com.codingapi.springboot.example.infrastructure.jap.repository.DemoEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class DemoRepositoryImpl implements DemoRepository {

    private final DemoEntityRepository demoEntityRepository;

    @Override
    public void save(Demo demo) {
        DemoEntity entity = new DemoEntity();
        entity.setId(demo.getId());
        entity.setName(demo.getName());

        demoEntityRepository.save(entity);
    }
}
