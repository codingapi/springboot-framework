package com.codingapi.example.app.query.service;

import com.codingapi.example.infra.flow.entity.FlowWorkEntity;
import com.codingapi.example.infra.flow.jpa.FlowWorkEntityRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlowWorkQueryService {

    private final FlowWorkEntityRepository flowWorkEntityRepository;

    public Page<FlowWorkEntity> list(SearchRequest searchRequest) {
        return flowWorkEntityRepository.searchRequest(searchRequest);
    }
}
