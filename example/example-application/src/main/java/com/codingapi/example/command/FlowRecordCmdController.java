package com.codingapi.example.command;

import com.codingapi.example.domain.User;
import com.codingapi.example.pojo.cmd.FlowCmd;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/cmd/flowRecord")
@AllArgsConstructor
public class FlowRecordCmdController {

    private final FlowService flowService;
    private final UserRepository userRepository;

    @PostMapping("/startFlow")
    public SingleResponse<FlowResult> startFlow(@RequestBody FlowCmd.StartFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        return SingleResponse.of(flowService.startFlow(request.getWorkCode(), current, request.getBindData(), request.getAdvice()));
    }


    @PostMapping("/trySubmitFlow")
    public SingleResponse<FlowSubmitResult> trySubmitFlow(@RequestBody FlowCmd.SubmitFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        if(request.getRecordId()>0) {
            return SingleResponse.of(flowService.trySubmitFlow(request.getRecordId(), current, request.getBindData(), request.getOpinion()));
        }else {
            return SingleResponse.of(flowService.trySubmitFlow(request.getWorkCode(), current, request.getBindData(), request.getOpinion()));
        }
    }


    @PostMapping("/submitFlow")
    public SingleResponse<FlowResult> submitFlow(@RequestBody FlowCmd.SubmitFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        if (current.isFlowManager()) {
            return SingleResponse.of(flowService.interfere(request.getRecordId(), current, request.getBindData(), request.getOpinion()));
        } else {
            return SingleResponse.of(flowService.submitFlow(request.getRecordId(), current, request.getBindData(), request.getOpinion()));
        }
    }

    @PostMapping("/custom")
    public SingleResponse<String> customFlowEvent(@RequestBody FlowCmd.CustomFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        return SingleResponse.of(flowService.customFlowEvent(request.getRecordId(), current, request.getButtonId(), request.getBindData(), request.getOpinion()));
    }


    @PostMapping("/save")
    public Response save(@RequestBody FlowCmd.SaveFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        flowService.save(request.getRecordId(), current, request.getBindData(), request.getAdvice());
        return Response.buildSuccess();
    }

    @PostMapping("/recall")
    public Response recall(@RequestBody FlowCmd.RecallFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        flowService.recall(request.getRecordId(), current);
        return Response.buildSuccess();
    }


    @PostMapping("/transfer")
    public Response transfer(@RequestBody FlowCmd.TransferFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        User target = userRepository.getUserById(request.getTargetUserId());
        flowService.transfer(request.getRecordId(), current, target, request.getBindData(), request.getAdvice());
        return Response.buildSuccess();
    }


    @PostMapping("/postponed")
    public Response postponed(@RequestBody FlowCmd.PostponedFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        flowService.postponed(request.getRecordId(), current, request.getTimeOut());
        return Response.buildSuccess();
    }


    @PostMapping("/urge")
    public Response urge(@RequestBody FlowCmd.UrgeFlow request) {
        User current = userRepository.getUserByUsername(request.getUserName());
        flowService.urge(request.getRecordId(), current);
        return Response.buildSuccess();
    }


}
