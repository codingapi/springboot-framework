package com.codingapi.example.infra.jpa;

import com.codingapi.example.infra.entity.TestEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface TestEntityRepository extends FastRepository<TestEntity,Long> {

}
