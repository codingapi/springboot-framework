package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;

import java.util.ArrayList;
import java.util.List;

public class FlowWorkRepositoryImpl implements FlowWorkRepository{

    private final List<FlowWork> cache = new ArrayList<>();

    @Override
    public FlowWork getFlowWorkById(long id) {
        return cache.stream().filter(flowWork -> flowWork.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void save(FlowWork flowWork) {
        if(flowWork.getId()==0){
            cache.add(flowWork);
            flowWork.setId(cache.size());
        }
    }

    @Override
    public void delete(long id) {
        cache.removeIf(flowWork -> flowWork.getId() == id);
    }
}
