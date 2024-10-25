package com.codingapi.example.query;

import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.example.jpa.FlowRecordEntityRepository;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/flowRecord")
@AllArgsConstructor
public class FlowRecordQueryController {

    private final FlowRecordEntityRepository flowRecordQuery;
    private final FlowService flowService;

    @GetMapping("/list")
    public MultiResponse<FlowRecordEntity> list(SearchRequest searchRequest) {
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findAll(pageRequest));
    }


    @GetMapping("/detail")
    public SingleResponse<FlowDetail> detail(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        return SingleResponse.of(flowService.detail(operatorId));
    }


    @GetMapping("/findTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findTodoByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findDoneByOperatorId")
    public MultiResponse<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findDoneByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findInitiatedByOperatorId")
    public MultiResponse<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findInitiatedByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findTimeoutTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findTimeoutTodoByOperatorId(operatorId, System.currentTimeMillis(), pageRequest));
    }


    @GetMapping("/findPostponedTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findPostponedTodoByOperatorId(operatorId, pageRequest));
    }


}
