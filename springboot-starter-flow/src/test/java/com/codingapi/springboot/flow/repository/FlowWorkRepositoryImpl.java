package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;

import java.util.ArrayList;
import java.util.List;

public class FlowWorkRepositoryImpl implements FlowWorkRepository {

    private final List<FlowWork> works = new ArrayList<>();

    @Override
    public void save(FlowWork flowWork) {
        if(flowWork.getId()==0){
            works.add(flowWork);
            flowWork.setId(works.size());
        }else {
            flowWork.setUpdateTime(System.currentTimeMillis());
        }
    }
}
