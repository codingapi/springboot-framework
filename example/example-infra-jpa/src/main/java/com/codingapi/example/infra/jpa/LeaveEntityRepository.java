package com.codingapi.example.infra.jpa;

import com.codingapi.example.infra.entity.LeaveEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface LeaveEntityRepository extends FastRepository<LeaveEntity,Long> {


    LeaveEntity getLeaveEntityById(long id);

}
