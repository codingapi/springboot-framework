package com.codingapi.example.app.query.service;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.repository.UserRepository;
import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.example.infra.flow.jpa.FlowRecordEntityRepository;
import com.codingapi.example.infra.flow.user.FlowUser;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlowRecordQueryService {

    private final FlowRecordEntityRepository flowRecordQuery;
    private final UserRepository userRepository;
    private final FlowService flowService;

    public Page<FlowRecordEntity> list(SearchRequest searchRequest) {
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize(), Sort.by("id").descending());
        return flowRecordQuery.findAllFlowRecords(pageRequest);
    }


    public FlowDetail detail(SearchRequest searchRequest) {
        long id = 0;
        if (searchRequest.getParameter("id") != null) {
            id = Long.parseLong(searchRequest.getParameter("id"));
        }
        String workCode = searchRequest.getParameter("workCode");
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        return flowService.detail(id, workCode, new FlowUser(user));
    }


    public Page<FlowRecordEntity> findAllByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return flowRecordQuery.findAllByOperatorId(user.getId(), pageRequest);
    }


    public Page<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return flowRecordQuery.findTodoByOperatorId(user.getId(), pageRequest);
    }


    public Page<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return flowRecordQuery.findDoneByOperatorId(user.getId(), pageRequest);
    }


    public Page<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return flowRecordQuery.findInitiatedByOperatorId(user.getId(), pageRequest);
    }


    public Page<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return flowRecordQuery.findTimeoutTodoByOperatorId(user.getId(), System.currentTimeMillis(), pageRequest);
    }


    public Page<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return flowRecordQuery.findPostponedTodoByOperatorId(user.getId(), pageRequest);
    }
}
