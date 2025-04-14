package com.codingapi.example.api.query;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.repository.UserRepository;
import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.example.infra.flow.jpa.FlowRecordEntityRepository;
import com.codingapi.example.infra.flow.user.FlowUser;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/flowRecord")
@AllArgsConstructor
public class FlowRecordQueryController {

    private final FlowRecordEntityRepository flowRecordQuery;
    private final UserRepository userRepository;
    private final FlowService flowService;

    @GetMapping("/list")
    public MultiResponse<FlowRecordEntity> list(SearchRequest searchRequest) {
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize(), Sort.by("id").descending());
        return MultiResponse.of(flowRecordQuery.findAllFlowRecords(pageRequest));
    }


    @GetMapping("/detail")
    public SingleResponse<FlowDetail> detail(SearchRequest searchRequest) {
        long id = 0;
        if (searchRequest.getParameter("id") != null) {
            id = Long.parseLong(searchRequest.getParameter("id"));
        }
        String workCode = searchRequest.getParameter("workCode");
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        return SingleResponse.of(flowService.detail(id, workCode, new FlowUser(user)));
    }



    @GetMapping("/findAllByOperatorId")
    public MultiResponse<FlowRecordEntity> findAllByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findAllByOperatorId(user.getId(), pageRequest));
    }


    @GetMapping("/findTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findTodoByOperatorId(user.getId(), pageRequest));
    }


    @GetMapping("/findDoneByOperatorId")
    public MultiResponse<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findDoneByOperatorId(user.getId(), pageRequest));
    }


    @GetMapping("/findInitiatedByOperatorId")
    public MultiResponse<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findInitiatedByOperatorId(user.getId(), pageRequest));
    }


    @GetMapping("/findTimeoutTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findTimeoutTodoByOperatorId(user.getId(), System.currentTimeMillis(), pageRequest));
    }


    @GetMapping("/findPostponedTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findPostponedTodoByOperatorId(user.getId(), pageRequest));
    }


}
