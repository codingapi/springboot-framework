package com.codingapi.example.controller;

import com.codingapi.example.pojo.FlowCreateRequest;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flow")
@AllArgsConstructor
public class FlowController {

    private final FlowWorkRepository flowWorkRepository;

    @GetMapping("/list")
    public MultiResponse<FlowWork> list(SearchRequest request) {
        return MultiResponse.of(flowWorkRepository.list(request));
    }

    @PostMapping("/create")
    public Response create(@RequestBody FlowCreateRequest flowCreateRequest) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(flowCreateRequest.getFlowWorkId());
        long operatorId = 1;
        IFlowOperator operator = FlowRepositoryContext.getInstance().getOperatorById(operatorId);
        flowWork.createNode(flowCreateRequest.getLeave(), operator);
        return Response.buildSuccess();
    }

}
