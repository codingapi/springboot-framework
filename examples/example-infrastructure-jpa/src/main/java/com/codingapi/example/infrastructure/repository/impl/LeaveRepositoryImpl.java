package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.domain.Leave;
import com.codingapi.example.infrastructure.convert.LeaveConvertor;
import com.codingapi.example.infrastructure.entity.LeaveEntity;
import com.codingapi.example.infrastructure.jpa.LeaveEntityRepository;
import com.codingapi.example.repository.LeaveRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class LeaveRepositoryImpl implements LeaveRepository {

    private final LeaveEntityRepository leaveEntityRepository;

    @Override
    public void save(Leave leave) {
        LeaveEntity entity = LeaveConvertor.convert(leave);
        entity = leaveEntityRepository.save(entity);
        leave.setId(entity.getId());
    }
}
