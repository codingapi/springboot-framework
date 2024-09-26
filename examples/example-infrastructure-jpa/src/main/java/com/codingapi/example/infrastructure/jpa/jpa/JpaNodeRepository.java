package com.codingapi.example.infrastructure.jpa.jpa;

import com.codingapi.example.domain.Node;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface JpaNodeRepository extends FastRepository<Node,Integer> {

}
