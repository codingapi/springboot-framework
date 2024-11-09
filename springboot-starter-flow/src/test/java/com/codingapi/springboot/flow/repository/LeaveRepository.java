package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.flow.Leave;

import java.util.ArrayList;
import java.util.List;

public class LeaveRepository {

    private final List<Leave> cache = new ArrayList<>();

    public void save(Leave leave) {
        if (leave.getId() == 0) {
            cache.add(leave);
            leave.setId(cache.size());
        }
    }
}
