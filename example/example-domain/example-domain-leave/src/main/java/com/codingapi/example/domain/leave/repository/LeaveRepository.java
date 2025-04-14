package com.codingapi.example.domain.leave.repository;


import com.codingapi.example.domain.leave.entity.Leave;

public interface LeaveRepository {

    void save(Leave leave);

    Leave getLeaveById(long id);


}
