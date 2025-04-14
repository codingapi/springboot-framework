package com.codingapi.example.infra.db.jpa;

import com.codingapi.example.infra.db.entity.TestEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface TestEntityRepository extends FastRepository<TestEntity,Long> {

}
