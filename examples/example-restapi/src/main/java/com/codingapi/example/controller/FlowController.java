package com.codingapi.example.controller;

import com.codingapi.example.pojo.FlowRequest;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/flow")
@AllArgsConstructor
public class FlowController {

    private final FlowWorkRepository flowWorkRepository;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public MultiResponse<FlowWork> list(SearchRequest request) {
        return MultiResponse.of(flowWorkRepository.list(request));
    }

    @PostMapping("/schema")
    public Response schema(@RequestBody FlowRequest.SchemaRequest request) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(request.getId());
        flowWork.setSchema(request.getSchema());
        flowWorkRepository.save(flowWork);
        return Response.buildSuccess();
    }

    @PostMapping("/save")
    public Response save(@RequestBody FlowRequest.BuildRequest request) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(request.getId());
        long operatorId =userRepository.getUserByUsername(TokenContext.current().getUsername()).getId();
        IFlowOperator operator = FlowRepositoryContext.getInstance().getOperatorById(operatorId);
        if(flowWork == null){
            flowWork = new FlowWork();
            flowWork.setEnable(true);
            flowWork.setLock(false);
            flowWork.setCreateUser(operator);
            flowWork.setCreateTime(System.currentTimeMillis());
        }
        flowWork.setTitle(request.getTitle());
        flowWork.setDescription(request.getDescription());
        flowWorkRepository.save(flowWork);
        return Response.buildSuccess();
    }

    @PostMapping("/create")
    public Response create(@RequestBody FlowRequest.CreateRequest createRequest) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(createRequest.getFlowWorkId());
        long operatorId =userRepository.getUserByUsername(TokenContext.current().getUsername()).getId();
        IFlowOperator operator = FlowRepositoryContext.getInstance().getOperatorById(operatorId);
        flowWork.createNode(createRequest.getLeave(), operator);
        return Response.buildSuccess();
    }

}
