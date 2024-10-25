package com.codingapi.example.repository;

import com.codingapi.example.domain.Leave;

public interface LeaveRepository {

    void save(Leave leave);

    Leave getLeaveById(long id);


}
