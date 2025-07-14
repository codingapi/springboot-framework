package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.flow.Leave;
import com.codingapi.springboot.flow.flow.Leave2;

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

    public void save(Leave2 leave2) {
        Leave leave = new Leave(leave2.getId(), leave2.getTitle(), leave2.getDays());
        this.save(leave);
    }
}
