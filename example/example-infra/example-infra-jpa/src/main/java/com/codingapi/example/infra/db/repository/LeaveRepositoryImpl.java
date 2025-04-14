package com.codingapi.example.infra.db.repository;

import com.codingapi.example.domain.leave.entity.Leave;
import com.codingapi.example.domain.leave.repository.LeaveRepository;
import com.codingapi.example.infra.db.convert.LeaveConvertor;
import com.codingapi.example.infra.db.entity.LeaveEntity;
import com.codingapi.example.infra.db.jpa.LeaveEntityRepository;
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

    @Override
    public Leave getLeaveById(long id) {
        return LeaveConvertor.convert(leaveEntityRepository.getLeaveEntityById(id));
    }

}
