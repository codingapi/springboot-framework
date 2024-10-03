package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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

    @Override
    public void delete(FlowWork flowWork) {
        works.remove(flowWork);
    }

    @Override
    public Page<FlowWork> list(SearchRequest pageRequest) {
        throw new RuntimeException("not support");
    }
}
