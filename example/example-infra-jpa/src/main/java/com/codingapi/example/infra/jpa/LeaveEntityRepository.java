package com.codingapi.example.infra.jpa;

import com.codingapi.example.infra.entity.LeaveEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveEntityRepository extends FastRepository<LeaveEntity,Long> {


    LeaveEntity getLeaveEntityById(long id);

}
