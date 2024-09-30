package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.LeaveEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface LeaveEntityRepository extends FastRepository<LeaveEntity,Long> {

}
