package com.codingapi.example.query;

import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.query.FlowRecordQuery;
import com.codingapi.springboot.flow.record.FlowRecord;
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

    private final FlowRecordQuery flowRecordQuery;
    private final FlowService flowService;

    @GetMapping("/list")
    public MultiResponse<FlowRecord> list(SearchRequest searchRequest) {
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findAll(pageRequest));
    }


    @GetMapping("/detail")
    public SingleResponse<FlowDetail> detail(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        return SingleResponse.of(flowService.detail(operatorId));
    }


    @GetMapping("/findTodoByOperatorId")
    public MultiResponse<FlowRecord> findTodoByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findTodoByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findDoneByOperatorId")
    public MultiResponse<FlowRecord> findDoneByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findDoneByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findInitiatedByOperatorId")
    public MultiResponse<FlowRecord> findInitiatedByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findInitiatedByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findTimeoutTodoByOperatorId")
    public MultiResponse<FlowRecord> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findTimeoutTodoByOperatorId(operatorId, pageRequest));
    }


    @GetMapping("/findPostponedTodoByOperatorId")
    public MultiResponse<FlowRecord> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        long operatorId = Long.parseLong(searchRequest.getParameter("operatorId"));
        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrent(), searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.findPostponedTodoByOperatorId(operatorId, pageRequest));
    }


}
