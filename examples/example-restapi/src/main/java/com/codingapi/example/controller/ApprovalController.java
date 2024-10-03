package com.codingapi.example.controller;

import com.codingapi.example.pojo.ApprovalRequest;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval")
@AllArgsConstructor
public class ApprovalController {

    private final FlowRecordRepository flowRecordRepository;
    private final UserRepository userRepository;

    @GetMapping("/todo")
    public MultiResponse<FlowRecord> todo() {
        long operatorId =userRepository.getUserByUsername(TokenContext.current().getUsername()).getId();
        return MultiResponse.of(flowRecordRepository.findTodoFlowRecordByOperatorId(operatorId));
    }

    @GetMapping("/done")
    public MultiResponse<FlowRecord> done() {
        long operatorId =userRepository.getUserByUsername(TokenContext.current().getUsername()).getId();
        return MultiResponse.of(flowRecordRepository.findDoneFlowRecordByOperatorId(operatorId));
    }

    @PostMapping("/submit")
    public Response submit(@RequestBody ApprovalRequest.SubmitRequest request) {
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(request.getRecordId());
        Opinion opinion = new Opinion(request.getOpinion(), request.isPass());
        flowRecord.submit(opinion);
        return Response.buildSuccess();
    }

    @PostMapping("/recall")
    public Response recall(@RequestBody ApprovalRequest.RecallRequest request) {
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(request.getRecordId());
        flowRecord.recall();
        return Response.buildSuccess();
    }

}
