package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;

import java.util.ArrayList;
import java.util.List;

public class FlowWorkRepositoryImpl implements FlowWorkRepository {

    private final List<FlowWork> works = new ArrayList<>();

    @Override
    public void save(FlowWork flowWork) {
        if (flowWork.getId() == 0) {
            works.add(flowWork);
            flowWork.setId(works.size());
        } else {
            flowWork.setUpdateTime(System.currentTimeMillis());
        }
    }

    @Override
    public FlowWork getFlowWorkById(long flowWorkId) {
        return works.stream().filter(flowWork -> flowWork.getId() == flowWorkId).findFirst().orElse(null);
    }
}
