package com.codingapi.example.infra.db.jpa;

import com.codingapi.example.infra.db.entity.LeaveEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface LeaveEntityRepository extends FastRepository<LeaveEntity,Long> {


    LeaveEntity getLeaveEntityById(long id);

}
