package com.codingapi.example.domain.leave.service;

import com.codingapi.example.domain.leave.entity.Leave;
import com.codingapi.example.domain.leave.repository.LeaveRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;

    public void create(Leave leave) {
        leaveRepository.save(leave);
    }
}
