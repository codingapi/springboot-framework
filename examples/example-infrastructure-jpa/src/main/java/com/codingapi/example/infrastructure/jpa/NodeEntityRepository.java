package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.NodeEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface NodeEntityRepository extends FastRepository<NodeEntity,Long> {

}
