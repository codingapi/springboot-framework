package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import org.springframework.data.domain.Page;

public interface FlowWorkRepository {

    void save(FlowWork flowWork);

    FlowWork getFlowWorkById(long flowWorkId);

    void delete(FlowWork flowWork);

    Page<FlowWork> list(SearchRequest searchRequest);

}
